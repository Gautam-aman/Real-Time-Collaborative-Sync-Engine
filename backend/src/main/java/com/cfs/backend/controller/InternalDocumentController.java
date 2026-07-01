package com.cfs.backend.controller;


import com.cfs.backend.Redis.RedisPublisher;
import com.cfs.backend.dto.DocumentEditEvent;
import com.cfs.backend.dto.OperationResult;
import com.cfs.backend.dto.RedisOperationEvent;
import com.cfs.backend.kafka.DocumentEventProducer;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.service.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalDocumentController {

	private final DocumentService documentService;
	private final SimpMessagingTemplate messagingTemplate;
	private final RedisPublisher redisPublisher;
	private final DocumentEventProducer producer;

	@PostMapping("/edit")
	public void internalEdit(@RequestBody OperationMessage message){

		OperationResult result = documentService.processOperation(message);

		producer.publish(DocumentEditEvent
				.builder()
				.documentId(result.getDocumentId())
				.userId(result.getUserId())
				.operation(result.getOperation())
				.position(result.getPosition())
				.value(result.getValue())
				.version(result.getVersion())
				.timestamp(System.currentTimeMillis())
				.build()
		);

		redisPublisher.publish(
				"document-events",
				RedisOperationEvent.builder()
						.operationResult(result)
						.build()
		);

		messagingTemplate.convertAndSend(
				"/topic/document/"
						+ message.getDocumentId(),
				result
		);
	}

}
