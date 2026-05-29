package com.aiquarrel.service;

import com.aiquarrel.model.dto.GenerateRequest;
import com.aiquarrel.model.dto.GenerateResponse;

public interface GenerateService {
    GenerateResponse generate(String deviceId, GenerateRequest request);
}
