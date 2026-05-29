package com.aiquarrel.controller;

import com.aiquarrel.model.dto.ApiResponse;
import com.aiquarrel.model.dto.FavoriteResponse;
import com.aiquarrel.model.dto.HistoryResponse;
import com.aiquarrel.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/favorite/{id}")
    public ApiResponse<FavoriteResponse> toggleFavorite(
            @RequestHeader("X-Device-Id") String deviceId,
            @PathVariable String id) {
        log.info("切换收藏: deviceId={}, generationId={}", deviceId, id);
        FavoriteResponse result = favoriteService.toggleFavorite(deviceId, id);
        return ApiResponse.success(result);
    }

    @GetMapping("/favorites")
    public ApiResponse<HistoryResponse> getFavorites(
            @RequestHeader("X-Device-Id") String deviceId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        log.info("查询收藏: deviceId={}, page={}, pageSize={}", deviceId, page, pageSize);
        if (pageSize > 50) pageSize = 50;
        if (page < 1) page = 1;
        HistoryResponse result = favoriteService.getFavorites(deviceId, page, pageSize);
        return ApiResponse.success(result);
    }
}
