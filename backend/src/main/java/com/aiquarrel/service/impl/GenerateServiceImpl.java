package com.aiquarrel.service.impl;

import com.aiquarrel.ai.OpenAiService;
import com.aiquarrel.exception.BizException;
import com.aiquarrel.model.dto.GenerateRequest;
import com.aiquarrel.model.dto.GenerateResponse;
import com.aiquarrel.model.entity.DeviceInfo;
import com.aiquarrel.model.entity.GenerationRecord;
import com.aiquarrel.model.enums.StyleEnum;
import com.aiquarrel.model.mapper.DeviceMapper;
import com.aiquarrel.model.mapper.GenerationMapper;
import com.aiquarrel.security.ContentFilter;
import com.aiquarrel.service.GenerateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateServiceImpl implements GenerateService {

    private final OpenAiService openAiService;
    private final ContentFilter contentFilter;
    private final GenerationMapper generationMapper;
    private final DeviceMapper deviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.rate-limit.daily-limit:50}")
    private int dailyLimit;

    @Value("${app.content.max-scene-length:200}")
    private int maxSceneLength;

    @Override
    @Transactional
    public GenerateResponse generate(String deviceId, GenerateRequest request) {
        // 校验场景长度 (40001)
        if (request.isSceneEmpty()) {
            throw new BizException(40001, "场景描述不能为空");
        }
        if (!request.isSceneLengthValid()) {
            throw new BizException(40001, "场景描述不能超过200字");
        }
        // 校验风格 (40002)
        if (!request.isStyleValid()) {
            throw new BizException(40002, "风格参数无效");
        }

        // 校验每日限额
        String dailyKey = "daily:" + deviceId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long dailyCount = redisTemplate.opsForValue().increment(dailyKey);
        if (dailyCount != null && dailyCount == 1) {
            redisTemplate.expire(dailyKey, 48, java.util.concurrent.TimeUnit.HOURS);
        }
        if (dailyCount != null && dailyCount > dailyLimit) {
            throw new BizException(42901, "今日生成次数已达上限，明天再来试试吧");
        }

        // 输入敏感词过滤
        String filteredScene = contentFilter.filter(request.getScene());
        if (filteredScene == null) {
            return buildRefuseResponse(request);
        }

        // 调用 AI 生成
        String content;
        try {
            content = openAiService.generateQuarrel(filteredScene, request.getStyle());
        } catch (Exception e) {
            log.error("AI生成失败", e);
            throw new BizException(50001, "AI服务异常，请稍后再试");
        }

        // 输出敏感词过滤
        if (!contentFilter.isSafe(content)) {
            content = "这个问题有点难，换个说法试试？";
        } else {
            String filteredContent = contentFilter.filter(content);
            content = filteredContent != null ? filteredContent : "这个问题有点难，换个说法试试？";
        }

        // 生成ID
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seqKey = "gen:seq:" + today;
        Long seq = redisTemplate.opsForValue().increment(seqKey);
        if (seq != null && seq == 1) {
            redisTemplate.expire(seqKey, 48, java.util.concurrent.TimeUnit.HOURS);
        }
        String recordId = String.format("gen_%s_%06d", today, seq != null ? seq : 1);

        // 持久化
        GenerationRecord record = new GenerationRecord();
        record.setId(recordId);
        record.setDeviceId(deviceId);
        record.setScene(request.getScene());
        record.setStyle(request.getStyle());
        record.setContent(content);
        record.setFavorited(0);
        record.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        record.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        generationMapper.insert(record);

        // 更新设备计数
        upsertDeviceCount(deviceId);

        // 缓存
        String cacheKey = "gen:" + recordId;
        redisTemplate.opsForHash().putAll(cacheKey, new java.util.HashMap<>());
        redisTemplate.expire(cacheKey, 30, java.util.concurrent.TimeUnit.MINUTES);

        return buildResponse(record);
    }

    private void upsertDeviceCount(String deviceId) {
        LocalDate today = LocalDate.now();
        DeviceInfo device = deviceMapper.selectOne(
                new LambdaQueryWrapper<DeviceInfo>()
                        .eq(DeviceInfo::getDeviceId, deviceId));

        if (device == null) {
            device = new DeviceInfo();
            device.setDeviceId(deviceId);
            device.setDailyCount(1);
            device.setDailyDate(today);
            device.setTotalCount(1);
            device.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
            device.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
            deviceMapper.insert(device);
        } else {
            if (device.getDailyDate() == null || !device.getDailyDate().equals(today)) {
                device.setDailyCount(1);
                device.setDailyDate(today);
            } else {
                device.setDailyCount(device.getDailyCount() + 1);
            }
            device.setTotalCount(device.getTotalCount() + 1);
            device.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
            deviceMapper.update(device, new LambdaUpdateWrapper<DeviceInfo>()
                    .eq(DeviceInfo::getDeviceId, deviceId));
        }
    }

    private GenerateResponse buildRefuseResponse(GenerateRequest request) {
        return GenerateResponse.builder()
                .id("")
                .scene(request.getScene())
                .style(request.getStyle())
                .content("这个问题有点难，换个说法试试？")
                .favorited(false)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z")
                .build();
    }

    private GenerateResponse buildResponse(GenerationRecord record) {
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
