package com.cfs.backend.controller;


import javax.lang.model.util.SimpleElementVisitor6;

import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.service.DocumentService;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DocumentController {

	private final DocumentService documentService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/edit")
	public void edit(OperationMessage message) {
		String latestDocument = documentService.applyOperation(message);
		messagingTemplate.convertAndSend("/topic/document" + message.getDocumentId(), latestDocument);
	}


}
