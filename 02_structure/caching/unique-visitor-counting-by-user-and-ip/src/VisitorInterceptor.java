package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.example.demo.config.Const.*;

@Component
@RequiredArgsConstructor
public class VisitorInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        int ip = ipToInt(getIp(request));
        int userHash = Math.abs("kye3314".hashCode());

        // 방문자수 카운트
        if (!ops.getBit(KEY_IP, ip) && !ops.getBit(KEY_USER, userHash)) {
            ops.increment(KEY_TOTAL_VISITOR);
            System.out.println("COUNTING! " + ops.get(KEY_TOTAL_VISITOR));
        }

        // 방문 기록
        ops.setBit(KEY_IP, ip, true);
        ops.setBit(KEY_USER, userHash,true);

        return true;
    }

    public String getIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("WL-Proxy-Client-IP");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_CLIENT_IP");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();

        return ip;
    }

    public static int ipToInt(String ip) {
        String[] octets = ip.split("\\.");
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(octets[i]);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException("IP address octets must be in the range 0-255");
            }
            result |= (octet << (8 * (3 - i))); // 8비트씩 왼쪽으로 이동
        }
        return result;
    }
}