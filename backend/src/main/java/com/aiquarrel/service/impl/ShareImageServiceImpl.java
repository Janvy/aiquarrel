package com.aiquarrel.service.impl;

import com.aiquarrel.exception.BizException;
import com.aiquarrel.model.dto.ShareImageResponse;
import com.aiquarrel.model.entity.GenerationRecord;
import com.aiquarrel.model.mapper.GenerationMapper;
import com.aiquarrel.service.ShareImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShareImageServiceImpl implements ShareImageService {

    private final GenerationMapper generationMapper;

    @Override
    public ShareImageResponse generateShareImage(String deviceId, String generationId) {
        GenerationRecord record = generationMapper.selectById(generationId);
        if (record == null) {
            throw new BizException(40003, "记录不存在");
        }
        if (!record.getDeviceId().equals(deviceId)) {
            throw new BizException(40005, "无权操作此记录");
        }

        // V1: 返回排版参数供前端 Canvas 渲染
        // 后续集成 CDN 上传后可返回实际图片 URL
        String imageUrl = "/api/v1/record/" + generationId;
        log.info("分享图片生成: generationId={}", generationId);

        return ShareImageResponse.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
