package com.cfs.backend.controller;


import com.cfs.backend.Redis.RedisPublisher;
import com.cfs.backend.cluster.ClusterNode;
import com.cfs.backend.cluster.ClusterProperties;
import com.cfs.backend.cluster.ConsistentHashRing;
import com.cfs.backend.cluster.RoutingService;
import com.cfs.backend.dto.DocumentEditEvent;
import com.cfs.backend.dto.JoinDocumentRequest;
import com.cfs.backend.dto.OperationResult;
import com.cfs.backend.dto.PresenceResponse;
import com.cfs.backend.dto.RedisOperationEvent;
import com.cfs.backend.dto.SessionInfo;
import com.cfs.backend.kafka.DocumentEventProducer;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.dto.SyncResponse;
import com.cfs.backend.service.DocumentService;
import com.cfs.backend.service.PresenceService;
import com.cfs.backend.service.SessionRegistry;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class DocumentController {

	private final DocumentService documentService;
	private final SimpMessagingTemplate messagingTemplate;
	private final PresenceService presenceService;
	private final SessionRegistry sessionRegistry;
	private final RedisPublisher redisPublisher;
	private final DocumentEventProducer producer;
	private final ConsistentHashRing consistentHashRing;
	private final ClusterProperties clusterProperties;
	private final RoutingService routingService;

	@MessageMapping("/edit")
	public void edit(OperationMessage message) {

		System.out.println(
				"EDIT RECEIVED : "
						+ message.getDocumentId()
		);


		ClusterNode owner = consistentHashRing.getOwner(message.getDocumentId());

		if(!owner.getNodeId().equals(clusterProperties.getNodeId())){
			routingService.forwardOperation(owner.getAddress(), message);
			return;
		}

		OperationResult result =
				documentService.processOperation(message);

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
		System.out.println(
				result.getDocumentId()
		);

		redisPublisher.publish(
				"document-events",
				RedisOperationEvent
						.builder()
						.operationResult(result)
						.build()
		);
	}


	@MessageMapping("/join")
	public void joinDocument(JoinDocumentRequest request , SimpMessageHeaderAccessor  accessor) {
		presenceService.join(request.getDocumentId(), request.getUserId());
		PresenceResponse response =
				PresenceResponse.builder()
						.documentId(request.getDocumentId())
						.users(presenceService.getUsers(request.getDocumentId()
								)
						)
						.build();
		String sessionId =
				accessor.getSessionId();

		sessionRegistry.register(sessionId ,  new SessionInfo(
				request.getUserId(),
				request.getDocumentId()
		));

		messagingTemplate.convertAndSend(
				"/topic/presence/"
						+ request.getDocumentId(),
				response
		);
	}


}
