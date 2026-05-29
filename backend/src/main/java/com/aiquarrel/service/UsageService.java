package com.aiquarrel.service;

import com.aiquarrel.model.dto.UsageResponse;

public interface UsageService {
    UsageResponse getUsage(String deviceId);
}
