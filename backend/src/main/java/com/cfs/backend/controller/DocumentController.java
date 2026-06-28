package com.cfs.backend.controller;


import com.cfs.backend.dto.OperationResult;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.dto.SyncResponse;
import com.cfs.backend.service.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class DocumentController {

	private final DocumentService documentService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/edit")
	public void edit(OperationMessage message) {

		OperationResult result =
				documentService.processOperation(message);

		messagingTemplate.convertAndSend(
				"/topic/document/"
						+ message.getDocumentId(),
				result
		);
	}


}
