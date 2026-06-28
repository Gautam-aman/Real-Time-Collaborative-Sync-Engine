package com.cfs.backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cfs.backend.model.Document;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import com.cfs.backend.dto.SyncResponse;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

	private final Map<String, Document> documents =
			new ConcurrentHashMap<>();

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
				 if(content.length() > 0 &&
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

	public Document getOrCreate(String id) {
		return getDocument(id);
	}
}
