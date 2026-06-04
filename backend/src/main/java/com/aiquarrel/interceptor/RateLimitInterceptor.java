package com.aiquarrel.interceptor;

import com.aiquarrel.model.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Cache<String, AtomicInteger> rateLimitCache;
    private final ObjectMapper objectMapper;

    @Value("${app.rate-limit.device-qps:10}")
    private int deviceQps;

    @Value("${app.rate-limit.ip-qps:5}")
    private int ipQps;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String deviceId = (String) request.getAttribute("deviceId");
        long windowSeconds = System.currentTimeMillis() / 1000;

        // 设备级限流
        if (deviceId != null) {
            String deviceKey = "rate:" + deviceId + ":" + windowSeconds;
            AtomicInteger counter = rateLimitCache.getIfPresent(deviceKey);
            if (counter == null) {
                counter = new AtomicInteger(1);
                rateLimitCache.put(deviceKey, counter);
            } else {
                int count = counter.incrementAndGet();
                if (count > deviceQps) {
                    log.warn("设备级限流触发: deviceId={}", deviceId);
                    writeRateLimitResponse(response);
                    return false;
                }
            }
        }

        // IP级限流兜底
        String ip = getClientIp(request);
        String ipKey = "rate:ip:" + ip + ":" + windowSeconds;
        AtomicInteger ipCounter = rateLimitCache.getIfPresent(ipKey);
        if (ipCounter == null) {
            ipCounter = new AtomicInteger(1);
            rateLimitCache.put(ipKey, ipCounter);
        } else {
            int count = ipCounter.incrementAndGet();
            if (count > ipQps) {
                log.warn("IP级限流触发: ip={}", ip);
                writeRateLimitResponse(response);
                return false;
            }
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
