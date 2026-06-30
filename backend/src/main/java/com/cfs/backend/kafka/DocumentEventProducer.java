package com.cfs.backend.kafka;


import com.cfs.backend.dto.DocumentEditEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentEventProducer {

	@Autowired
	private final KafkaTemplate<String, DocumentEditEvent> kafkaTemplate;

	public void publish(DocumentEditEvent documentEditEvent) {

		kafkaTemplate.send("document-event" , documentEditEvent.getDocumentId() , documentEditEvent);
		// all events of same doc stay together id

	}

}
