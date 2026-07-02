package com.cfs.backend.config;

import com.cfs.backend.Redis.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

	@Bean
	public RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory, RedisSubscriber subscriber, ChannelTopic topic) {
		RedisMessageListenerContainer
				container =
				new RedisMessageListenerContainer();

		container.setConnectionFactory(
				factory
		);

		container.addMessageListener(
				subscriber,
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
