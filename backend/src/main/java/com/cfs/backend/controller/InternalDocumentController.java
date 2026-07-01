package com.cfs.backend.controller;


import com.cfs.backend.dto.OperationResult;
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

	@PostMapping("/edit")
	public void internalEdit(@RequestBody OperationMessage message){

		OperationResult result = documentService.processOperation(message);

		messagingTemplate.convertAndSend(
				"/topic/document/"
						+ message.getDocumentId(),
				result
		);
	}

}
