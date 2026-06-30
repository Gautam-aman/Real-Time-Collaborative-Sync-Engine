package com.cfs.backend.kafka;

import com.cfs.backend.dto.DocumentEditEvent;
import com.cfs.backend.model.Document;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class DocumentEventConsumer {

	@KafkaListener(topics = "document-events" ,groupId = "sync-engine")
	public void consume(DocumentEditEvent documentEditEvent) {
		System.out.println("EVENT => " + documentEditEvent);
	}

}
