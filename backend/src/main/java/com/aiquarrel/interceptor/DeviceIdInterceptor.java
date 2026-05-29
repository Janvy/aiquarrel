package com.aiquarrel.interceptor;

import com.aiquarrel.model.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceIdInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String deviceId = request.getHeader("X-Device-Id");

        if (!StringUtils.hasText(deviceId)) {
            log.warn("请求缺少X-Device-Id头: uri={}, ip={}", request.getRequestURI(), request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ApiResponse<Void> errorResp = ApiResponse.error(40004, "缺少X-Device-Id请求头");
            response.getWriter().write(objectMapper.writeValueAsString(errorResp));
            return false;
        }

        try {
            UUID.fromString(deviceId);
        } catch (IllegalArgumentException e) {
            log.warn("X-Device-Id格式无效: {}", deviceId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ApiResponse<Void> errorResp = ApiResponse.error(40004, "X-Device-Id格式无效，需为UUID格式");
            response.getWriter().write(objectMapper.writeValueAsString(errorResp));
            return false;
        }

        request.setAttribute("deviceId", deviceId);
        return true;
    }
}
