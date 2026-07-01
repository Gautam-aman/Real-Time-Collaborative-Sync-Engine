package com.cfs.backend.kafka;

import com.cfs.backend.dto.DocumentEditEvent;
import com.cfs.backend.dto.DocumentEventEntity;
import com.cfs.backend.model.Document;
import com.cfs.backend.repository.DocumentEventRepository;
import org.w3c.dom.events.DocumentEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class DocumentEventConsumer {

	private DocumentEventRepository documentEventRepository;

	@KafkaListener(topics = "document-events" ,groupId = "sync-engine")
	public void consume(DocumentEditEvent event) {
		System.out.println("EVENT => " + event);

		documentEventRepository.save(
				DocumentEventEntity.builder()
						.documentId(event.getDocumentId()).userId(event.getUserId())
						.operation(event.getOperation().name())
						.position(event.getPosition())
						.value(event.getValue())
						.version(event.getVersion())
						.timestamp(event.getTimestamp())
						.build()
		);

	}

}
