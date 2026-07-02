package com.cfs.backend.Redis;

import com.cfs.backend.dto.RedisOperationEvent;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

	private final ObjectMapper mapper;
	private final SimpMessagingTemplate messagingTemplate;


	@Override
	public void onMessage(Message message, byte  [] pattern) {
		try {
			String payload = new String(message.getBody());
			RedisOperationEvent event = mapper.readValue(payload, RedisOperationEvent.class);
			messagingTemplate.convertAndSend(
					"/topic/document/"
							+ event.getOperationResult()
							.getDocumentId(),

					event.getOperationResult()
			);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
