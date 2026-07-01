package com.cfs.backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cfs.backend.dto.AppliedOperation;
import com.cfs.backend.dto.OperationResult;
import com.cfs.backend.model.Document;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import com.cfs.backend.dto.SyncResponse;
import com.cfs.backend.repository.DocumentSnapshotRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final Map<String, Document> documents = new ConcurrentHashMap<>();
	private final OperationTransformService operationTransformService;
	private final SnapshotService snapshotService;
	private final DocumentSnapshotRepository documentSnapshotRepository;

	public Document getDocument(String documentId) {
		if (!documents.containsKey(documentId)) {
			documents.put(documentId, new Document(documentId));
		}
		return documents.get(documentId);
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

			 if (message.getOperation() == OperationType.INSERT){
				 int position = Math.min(content.length(), message.getPosition());
				 content.insert(position , message.getValue());
			 }

			 if (message.getOperation() == OperationType.DELETE){
				 if(!content.isEmpty() &&
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

		} ̰


	}

	public Document loadDocument(String documentId) {
		return documents.computeIfAbsent(
				documentId,
				id -> {
					return documentSnapshotRepository.findById(id)
							.map(snapshot -> {
								Document document = new Document(id);
								document.getContent().append(snapshot.getContent());
								document.setVersion(snapshot.getVersion());
								return document;
							})
							.orElse(
									new Document(id)
							);
				}
		);
	}

	public OperationResult processOperation(OperationMessage message) {
		Document document = getDocument(message.getDocumentId());
		synchronized (document){


			if (message.getVersion() < document.getVersion()) {
				for(AppliedOperation it : document.getHistory()){
					if (it.getVersion() > message.getVersion()) {
						operationTransformService.transform(message,it);
					}
				}
			}

			if(message.getPosition() == -1){
				return OperationResult.builder()
						.documentId(message.getDocumentId())
						.version(document.getVersion())
						.build();
			}

			StringBuilder content = document.getContent();

			if (message.getOperation() == OperationType.INSERT) {
				content.insert(
					Math.min(message.getPosition() , content.length()) ,
					message.getValue()
				);
			}

			if (message.getOperation() == OperationType.DELETE) {
				if (!content.isEmpty() &&
				message.getPosition()
								< content.length()) {
					content.deleteCharAt(message.getPosition());
				}
			}


			document.getHistory()
					.add(AppliedOperation
									.builder()
									.version(document.getVersion()+1)
									.operation(message.getOperation())
									.position(message.getPosition())
									.value(message.getValue())
									.userId(message.getUserId())
									.build()
					);
			document.setVersion(document.getVersion() + 1);

			if(document.getOperationsSinceSnapshot() >= 100){
				snapshotService.saveSnapshot(document);
				document.setOperationsSinceSnapshot(0);
			}

			document.setOperationsSinceSnapshot(document.getOperationsSinceSnapshot() + 1);

			if(document.getHistory().size() > 1000){
				document.getHistory().removeFirst();
			}



			return OperationResult.builder()
					.documentId(message.getDocumentId())
					.operation(message.getOperation())
					.position(message.getPosition())
					.value(message.getValue())
					.version(message.getVersion())
					.userId(message.getUserId())
					.build();

		}
	}

}
