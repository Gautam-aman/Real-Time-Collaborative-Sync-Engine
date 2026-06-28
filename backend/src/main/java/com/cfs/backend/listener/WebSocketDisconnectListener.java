package com.cfs.backend.listener;

import com.cfs.backend.dto.PresenceResponse;
import com.cfs.backend.dto.SessionInfo;
import com.cfs.backend.service.PresenceService;
import com.cfs.backend.service.SessionRegistry;
import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener {
	private final SessionRegistry sessionRegistry;
	private final PresenceService presenceService;
	private final SimpMessagingTemplate messagingTemplate;

	@EventListener
	public void handleDisconnect(SessionDisconnectEvent event){
		String sessionId = event.getSessionId();

		SessionInfo sessionInfo =
				sessionRegistry.remove(
						sessionId
				);

		if(sessionInfo == null) {
			return;
		}

		presenceService.leave(
				sessionInfo.getDocumentId(),
				sessionInfo.getUserId()
		);

		PresenceResponse response =
				PresenceResponse.builder()
						.documentId(
								sessionInfo.getDocumentId()
						)
						.users(
								presenceService.getUsers(
										sessionInfo.getDocumentId()
								)
						)
						.build();

		messagingTemplate.convertAndSend(
				"/topic/presence/"
						+ sessionInfo.getDocumentId(),
				response
		);

	}

}
