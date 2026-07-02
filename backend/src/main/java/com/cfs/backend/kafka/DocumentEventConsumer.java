package com.cfs.backend.kafka;

import com.cfs.backend.dto.DocumentEditEvent;
import com.cfs.backend.dto.DocumentEventEntity;
import com.cfs.backend.repository.DocumentEventRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentEventConsumer {

	private final DocumentEventRepository documentEventRepository;

	@KafkaListener(topics = "document-events" ,groupId = "sync-engine")
	public void consume(DocumentEditEvent event) {
		System.out.println("EVENT => " + event);

		documentEventRepository.save(
				DocumentEventEntity.builder()
						.documentId(event.getDocumentId()).userId(event.getUserId())
						.operation(event.getOperation() != null ? event.getOperation().name() : null)
						.position(event.getPosition())
						.value(event.getValue())
						.version(event.getVersion())
						.timestamp(event.getTimestamp())
						.build()
		);

	}

}
