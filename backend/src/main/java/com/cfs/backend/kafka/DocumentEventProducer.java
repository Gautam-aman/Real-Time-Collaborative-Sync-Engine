package com.cfs.backend.kafka;


import com.cfs.backend.dto.DocumentEditEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentEventProducer {

	private final KafkaTemplate<String, DocumentEditEvent> kafkaTemplate;

	public void publish(DocumentEditEvent documentEditEvent) {

		kafkaTemplate.send("document-events" , documentEditEvent.getDocumentId() , documentEditEvent);
		// all events of same doc stay together id

	}

}
