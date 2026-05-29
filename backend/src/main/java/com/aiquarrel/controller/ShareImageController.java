package com.aiquarrel.controller;

import com.aiquarrel.model.dto.ApiResponse;
import com.aiquarrel.model.dto.ShareImageRequest;
import com.aiquarrel.model.dto.ShareImageResponse;
import com.aiquarrel.service.ShareImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ShareImageController {

    private final ShareImageService shareImageService;

    @PostMapping("/share-image")
    public ApiResponse<ShareImageResponse> generateShareImage(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody ShareImageRequest request) {
        log.info("生成分享图片: deviceId={}, id={}", deviceId, request.getId());
        ShareImageResponse result = shareImageService.generateShareImage(deviceId, request.getId());
        return ApiResponse.success(result);
    }
}
