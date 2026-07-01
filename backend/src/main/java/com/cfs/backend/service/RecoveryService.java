package com.cfs.backend.service;

import java.util.List;

import com.cfs.backend.dto.DocumentEventEntity;
import com.cfs.backend.model.Document;
import com.cfs.backend.repository.DocumentEventRepository;
import com.cfs.backend.repository.DocumentSnapshotRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecoveryService {
	private final
	DocumentSnapshotRepository snapshotRepository;
	private final DocumentEventRepository eventRepository;



	public Document Recover(String documentId) {

		Document document = new Document(documentId);

		snapshotRepository.findById(documentId)
				.ifPresent(snapshot -> {
					document.getContent().append(snapshot.getContent());
					document.setVersion(snapshot.getVersion());
				});

		List<DocumentEventEntity> events = eventRepository.findByDocumentIdAndVersionGreaterThanOrderByVersionAsc(
								documentId,
								document.getVersion());
		replay(document, events);
		return document;

	}

	private void replay(Document document, List<DocumentEventEntity> events) {
			StringBuilder content = document.getContent();
			for(DocumentEventEntity event : events){
				if("INSERT".equals(event.getOperation())){
					content.insert(Math.min(event.getPosition(), content.length()), event.getValue()
					);
				}
				if("DELETE".equals(event.getOperation())){
					if(!content.isEmpty() && event.getPosition() < content.length()){
						content.deleteCharAt(
								event.getPosition()
						);
					}
				}
				document.setVersion(
						event.getVersion()
				);
	}

}}
