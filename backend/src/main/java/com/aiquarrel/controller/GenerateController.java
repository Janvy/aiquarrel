package com.aiquarrel.controller;

import com.aiquarrel.model.dto.ApiResponse;
import com.aiquarrel.model.dto.GenerateRequest;
import com.aiquarrel.model.dto.GenerateResponse;
import com.aiquarrel.service.GenerateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class GenerateController {

    private final GenerateService generateService;

    @PostMapping("/generate")
    public ApiResponse<GenerateResponse> generate(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody GenerateRequest request) {
        log.info("生成请求: deviceId={}, scene={}, style={}", deviceId, request.getScene(), request.getStyle());
        GenerateResponse result = generateService.generate(deviceId, request);
        return ApiResponse.success(result);
    }
}
