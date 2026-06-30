package com.cfs.backend.Redis;

import com.cfs.backend.dto.RedisOperationEvent;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

	private final ObjectMapper mapper;
	private final SimpMessagingTemplate messagingTemplate;


	@Override
	public void onMessage(Message message, byte @Nullable [] pattern) {
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
