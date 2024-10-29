package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

@Configuration
public class ListenerContainer {

    @Bean
    public StreamMessageListenerContainer streamMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        return StreamMessageListenerContainer.create(connectionFactory);
    }
}