package com.aiquarrel.controller;

import com.aiquarrel.model.dto.ApiResponse;
import com.aiquarrel.model.dto.HistoryResponse;
import com.aiquarrel.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/history")
    public ApiResponse<HistoryResponse> getHistory(
            @RequestHeader("X-Device-Id") String deviceId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        log.info("查询历史: deviceId={}, page={}, pageSize={}", deviceId, page, pageSize);
        if (pageSize > 50) pageSize = 50;
        if (page < 1) page = 1;
        HistoryResponse result = historyService.getHistory(deviceId, page, pageSize);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/history/{id}")
    public ApiResponse<Void> deleteHistory(
            @RequestHeader("X-Device-Id") String deviceId,
            @PathVariable String id) {
        log.info("删除历史: deviceId={}, id={}", deviceId, id);
        historyService.deleteHistory(deviceId, id);
        return ApiResponse.success();
    }

    @DeleteMapping("/history")
    public ApiResponse<Void> clearHistory(
            @RequestHeader("X-Device-Id") String deviceId) {
        log.info("清空历史: deviceId={}", deviceId);
        historyService.clearHistory(deviceId);
        return ApiResponse.success();
    }
}
