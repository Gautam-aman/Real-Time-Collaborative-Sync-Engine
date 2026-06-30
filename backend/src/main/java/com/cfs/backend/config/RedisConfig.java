package com.cfs.backend.config;

import com.cfs.backend.Redis.RedisSubscriber;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Configuration
public class RedisConfig {

	@Bean
	public MessageListenerAdapter listenerAdapter(
			RedisSubscriber subscriber) {
			return new MessageListenerAdapter(subscriber);
	}

	@Bean
	public RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory, MessageListenerAdapter adapter, ChannelTopic topic) {
		RedisMessageListenerContainer
				container =
				new RedisMessageListenerContainer();

		container.setConnectionFactory(
				factory
		);

		container.addMessageListener(
				adapter,
				topic
		);

		return container;
	}

	@Bean
	public ChannelTopic
	documentTopic() {

		return new ChannelTopic(
				"document-events"
		);
	}
}
