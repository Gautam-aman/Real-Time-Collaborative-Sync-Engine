package com.cfs.backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cfs.backend.model.Document;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

	private final Map<String, Document> documents =
			new ConcurrentHashMap<>();

	private Document getDocument(String documentId) {
		if (!documents.containsKey(documentId)) {
			documents.put(documentId, new Document(documentId));
		}
		return documents.get(documentId);
	}

	public String applyOperation(OperationMessage message) {
		Document document = getDocument(message.getDocumentId());

		synchronized (document) {
			StringBuilder content = document.getContent();

			if (message.getOperation() == OperationType.INSERT) {
				int position = Math.min(content.length(), message.getPosition());
				content.insert(position, message.getValue());
			}

			if (message.getOperation() == OperationType.DELETE) {
				if (content.length() > 0 && message.getPosition() < content.length()) {
					content.deleteCharAt(message.getPosition());
				}
			}

			return content.toString();

		}
	}




}
