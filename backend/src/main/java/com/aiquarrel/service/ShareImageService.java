package com.aiquarrel.service;

import com.aiquarrel.model.dto.ShareImageResponse;

public interface ShareImageService {
    ShareImageResponse generateShareImage(String deviceId, String generationId);
}
