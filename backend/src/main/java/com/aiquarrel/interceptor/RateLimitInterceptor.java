package com.aiquarrel.interceptor;

import com.aiquarrel.model.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.rate-limit.device-qps:10}")
    private int deviceQps;

    @Value("${app.rate-limit.ip-qps:5}")
    private int ipQps;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String deviceId = (String) request.getAttribute("deviceId");
        long windowSeconds = System.currentTimeMillis() / 1000;

        if (deviceId != null) {
            String deviceKey = "rate:" + deviceId + ":" + windowSeconds;
            Long count = redisTemplate.opsForValue().increment(deviceKey);
            if (count != null && count == 1) {
                redisTemplate.expire(deviceKey, 2, TimeUnit.SECONDS);
            }
            if (count != null && count > deviceQps) {
                log.warn("设备级限流触发: deviceId={}", deviceId);
                writeRateLimitResponse(response);
                return false;
            }
        }

        String ip = getClientIp(request);
        String ipKey = "rate:ip:" + ip + ":" + windowSeconds;
        Long ipCount = redisTemplate.opsForValue().increment(ipKey);
        if (ipCount != null && ipCount == 1) {
            redisTemplate.expire(ipKey, 2, TimeUnit.SECONDS);
        }
        if (ipCount != null && ipCount > ipQps) {
            log.warn("IP级限流触发: ip={}", ip);
            writeRateLimitResponse(response);
            return false;
        }

        return true;
    }

    private void writeRateLimitResponse(HttpServletResponse response) throws Exception {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResponse<Void> errorResp = ApiResponse.error(42902, "请求频率过高，请稍后再试");
        response.getWriter().write(objectMapper.writeValueAsString(errorResp));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
