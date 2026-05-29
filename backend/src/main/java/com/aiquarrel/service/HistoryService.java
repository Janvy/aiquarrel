package com.aiquarrel.service;

import com.aiquarrel.model.dto.HistoryResponse;

public interface HistoryService {
    HistoryResponse getHistory(String deviceId, int page, int pageSize);
    void deleteHistory(String deviceId, String id);
    void clearHistory(String deviceId);
}
