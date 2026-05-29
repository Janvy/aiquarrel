package com.aiquarrel.controller;

import com.aiquarrel.model.dto.ApiResponse;
import com.aiquarrel.model.dto.GenerateResponse;
import com.aiquarrel.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/record/{id}")
    public ApiResponse<GenerateResponse> getRecord(
            @RequestHeader("X-Device-Id") String deviceId,
            @PathVariable String id) {
        log.info("查询记录详情: deviceId={}, id={}", deviceId, id);
        GenerateResponse result = recordService.getRecord(deviceId, id);
        return ApiResponse.success(result);
    }
}
