package com.aiquarrel.service.impl;

import com.aiquarrel.exception.BizException;
import com.aiquarrel.model.dto.GenerateResponse;
import com.aiquarrel.model.entity.GenerationRecord;
import com.aiquarrel.model.mapper.GenerationMapper;
import com.aiquarrel.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final GenerationMapper generationMapper;

    @Override
    public GenerateResponse getRecord(String deviceId, String recordId) {
        GenerationRecord record = generationMapper.selectById(recordId);
        if (record == null) {
            throw new BizException(40003, "记录不存在");
        }
        if (!record.getDeviceId().equals(deviceId)) {
            throw new BizException(40005, "无权查看此记录");
        }

        return GenerateResponse.builder()
                .id(record.getId())
                .scene(record.getScene())
                .style(record.getStyle())
                .content(record.getContent())
                .favorited(record.getFavorited() == 1)
                .createdAt(record.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z")
                .build();
    }
}
