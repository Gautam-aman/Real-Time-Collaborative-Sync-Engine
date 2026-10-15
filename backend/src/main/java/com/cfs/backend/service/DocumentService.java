package com.cfs.backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cfs.backend.Metrics.MetricsService;
import com.cfs.backend.cluster.ClusterNode;
import com.cfs.backend.cluster.ConsistentHashRing;
import com.cfs.backend.dto.AppliedOperation;
import com.cfs.backend.dto.OperationResult;
import com.cfs.backend.model.Document;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import com.cfs.backend.dto.SyncResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final Map<String, Document> documents = new ConcurrentHashMap<>();
	private final OperationTransformService operationTransformService;
	private final SnapshotService snapshotService;
	private final RecoveryService recoveryService;
	private final ConsistentHashRing consistentHashRing;
	private final MetricsService metricsService;

	public Document getDocument(String documentId) {
		return loadDocument(documentId);
	}

	public SyncResponse applyOperation(OperationMessage message) {

		Document document = getDocument(message.getDocumentId());

		synchronized (document) {
			if (message.getVersion() != document.getVersion()) {
				return SyncResponse.builder()
						.documentId(document.getId())
						.content(document.getContent().toString())
						.version(document.getVersion())
						.build();
			}

			StringBuilder content = document.getContent();

			if (message.getOperation() == OperationType.INSERT) {
				int position = Math.min(content.length(), message.getPosition());
				content.insert(position, message.getValue());
			}

			if (message.getOperation() == OperationType.DELETE) {
				if (!content.isEmpty() &&
						message.getPosition()
								< content.length()) {

					content.deleteCharAt(
							message.getPosition()
					);
				}
			}

			document.setVersion(document.getVersion() + 1);
			return SyncResponse.builder()
					.documentId(document.getId())
					.content(document.getContent().toString())
					.version(document.getVersion())
					.build();

		}
	}

	public Document loadDocument(String documentId) {
		return documents.computeIfAbsent(
				documentId,
				recoveryService::recover
		);
	}

	public OperationResult processOperation(OperationMessage message) {
		validateIncomingOperation(message);

		Document document = getDocument(message.getDocumentId());
		synchronized (document){

			if (message.getVersion() > document.getVersion()) {
				throw new IllegalArgumentException(
						"Client version is ahead of server version"
				);
			}

			if (message.getVersion() < document.getVersion()) {
				for(AppliedOperation it : document.getHistory()){
					if (it.getVersion() > message.getVersion()) {
						operationTransformService.transform(message,it);
					}
				}
			}

			if(message.getPosition() == -1 && message.getOperation() == OperationType.DELETE){
				return OperationResult.builder()
						.documentId(message.getDocumentId())
						.version(document.getVersion())
						.build();
			}

			StringBuilder content = document.getContent();
			int position = message.getPosition();
			if (message.getOperation() == OperationType.INSERT) {
				if (position < 0 || position > content.length()) {
					throw new IllegalArgumentException(
							"Insert position is outside document bounds"
					);
				}
			} else if (message.getOperation() == OperationType.DELETE) {
				if (position < 0 || position >= content.length()) {
					throw new IllegalArgumentException(
							"Delete position is outside document bounds"
					);
				}
			}

			if (message.getOperation() == OperationType.INSERT) {
				content.insert(position, message.getValue());

			} else if (message.getOperation() == OperationType.DELETE) {
				content.deleteCharAt(position);
			}

			logOwnership(message.getDocumentId());

			long nextVersion = document.getVersion() + 1;

			document.getHistory()
					.add(AppliedOperation
									.builder()
									.version(nextVersion)
									.operation(message.getOperation())
									.position(message.getPosition())
									.value(message.getValue())
									.userId(message.getUserId())
									.build()
					);
			document.setVersion(nextVersion);

			document.setOperationsSinceSnapshot(document.getOperationsSinceSnapshot() + 1);
			metricsService.incrementOperations();
			metricsService.setActiveDocuments(documents.size());

			if(document.getOperationsSinceSnapshot() >= 100){
				snapshotService.saveSnapshot(document);
				document.setOperationsSinceSnapshot(0);
			}

			if(document.getHistory().size() > 1000){
				document.getHistory().remove(0);
			}



			return OperationResult.builder()
					.documentId(message.getDocumentId())
					.operation(message.getOperation())
					.position(message.getPosition())
					.value(message.getValue())
					.version(document.getVersion())
					.userId(message.getUserId())
					.build();

		}
	}

	private void logOwnership(String documentId) {
		ClusterNode owner =
				consistentHashRing.getOwner(
						documentId
				);
		System.out.println("Document " + documentId + " owned by " + owner.getNodeId()
		);
	}

	private void validateIncomingOperation(OperationMessage message) {

		if (message.getOperation() == null) {
			throw new IllegalArgumentException(
					"Operation type is required"
			);
		}

		if (message.getPosition() == null) {
			throw new IllegalArgumentException(
					"Operation position is required"
			);
		}

		if (message.getPosition() < 0) {
			throw new IllegalArgumentException(
					"Operation position cannot be negative"
			);
		}

		if (message.getVersion() < 0) {
			throw new IllegalArgumentException(
					"Document version cannot be negative"
			);
		}

		if (message.getOperation() == OperationType.INSERT
				&& message.getValue() == null) {
			throw new IllegalArgumentException(
					"Insert value is required"
			);
		}
	}

}
