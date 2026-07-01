package com.cfs.backend.Redis;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;

	public void publish(String channel, Object event){
		try{
			String json = objectMapper.writeValueAsString(event);
			stringRedisTemplate.convertAndSend(channel, json);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}

}
