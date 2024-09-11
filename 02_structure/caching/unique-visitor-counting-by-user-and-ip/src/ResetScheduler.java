package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.example.demo.config.Const.*;

@Component
@RequiredArgsConstructor
public class ResetScheduler {

    private final RedisTemplate<String, String> redisTemplate;


    @Scheduled(cron = "0 0 0 * * *")
    public void resetVisitorCount() {

        // 방문여부 초기화
        redisTemplate.delete(KEY_IP);
        redisTemplate.delete(KEY_USER);

        // 방문자 수 초기화
        redisTemplate.delete(KEY_TOTAL_VISITOR);

        System.out.println("===== Redis Cache Reset =====");
    }
}