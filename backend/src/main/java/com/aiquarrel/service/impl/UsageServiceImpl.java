package com.aiquarrel.service.impl;

import com.aiquarrel.model.dto.UsageResponse;
import com.aiquarrel.model.entity.DeviceInfo;
import com.aiquarrel.model.mapper.DeviceMapper;
import com.aiquarrel.service.UsageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageServiceImpl implements UsageService {

    private final DeviceMapper deviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.rate-limit.daily-limit:50}")
    private int dailyLimit;

    @Override
    public UsageResponse getUsage(String deviceId) {
        // 从 Redis 获取当日计数
        String dailyKey = "daily:" + deviceId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer dailyCount = (Integer) redisTemplate.opsForValue().get(dailyKey);
        if (dailyCount == null) {
            dailyCount = 0;
        }

        // 从 MySQL 获取累计计数
        DeviceInfo device = deviceMapper.selectOne(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeviceId, deviceId));
        int totalCount = device != null ? device.getTotalCount() : 0;

        return UsageResponse.builder()
                .dailyCount(dailyCount)
                .totalCount(totalCount)
                .dailyLimit(dailyLimit)
                .build();
    }
}
