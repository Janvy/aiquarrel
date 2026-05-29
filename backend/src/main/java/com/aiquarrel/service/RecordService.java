package com.aiquarrel.service;

import com.aiquarrel.model.dto.GenerateResponse;

public interface RecordService {
    GenerateResponse getRecord(String deviceId, String recordId);
}
