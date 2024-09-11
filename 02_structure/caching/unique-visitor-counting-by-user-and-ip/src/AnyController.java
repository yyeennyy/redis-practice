package com.example.demo;

import com.example.demo.dto.VisitorCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.config.Const.KEY_TOTAL_VISITOR;

@RestController
@RequiredArgsConstructor
public class AnyController {
    private final RedisTemplate<String, String> redisTemplate;

    // 테스트용 API
    @GetMapping("/")
    public VisitorCountDto index() {

        String count = redisTemplate.opsForValue().get(KEY_TOTAL_VISITOR);

        return new VisitorCountDto(count);
    }
}