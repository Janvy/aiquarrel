package com.aiquarrel.controller;

import com.aiquarrel.model.dto.ApiResponse;
import com.aiquarrel.model.dto.UsageResponse;
import com.aiquarrel.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/usage")
    public ApiResponse<UsageResponse> getUsage(
            @RequestHeader("X-Device-Id") String deviceId) {
        log.info("查询使用次数: deviceId={}", deviceId);
        UsageResponse result = usageService.getUsage(deviceId);
        return ApiResponse.success(result);
    }
}
