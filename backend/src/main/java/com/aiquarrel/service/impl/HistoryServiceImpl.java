package com.aiquarrel.service.impl;

import com.aiquarrel.exception.BizException;
import com.aiquarrel.model.dto.HistoryResponse;
import com.aiquarrel.model.entity.FavoriteRecord;
import com.aiquarrel.model.entity.GenerationRecord;
import com.aiquarrel.model.enums.StyleEnum;
import com.aiquarrel.model.mapper.FavoriteMapper;
import com.aiquarrel.model.mapper.GenerationMapper;
import com.aiquarrel.service.HistoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final GenerationMapper generationMapper;
    private final FavoriteMapper favoriteMapper;

    @Value("${app.content.content-preview-length:30}")
    private int previewLength;

    @Override
    public HistoryResponse getHistory(String deviceId, int page, int pageSize) {
        Page<GenerationRecord> pageReq = new Page<>(page, pageSize);
        LambdaQueryWrapper<GenerationRecord> wrapper = new LambdaQueryWrapper<GenerationRecord>()
                .eq(GenerationRecord::getDeviceId, deviceId)
                .orderByDesc(GenerationRecord::getCreatedAt);

        Page<GenerationRecord> result = generationMapper.selectPage(pageReq, wrapper);

        List<HistoryResponse.HistoryItem> list = result.getRecords().stream()
                .map(this::toHistoryItem)
                .collect(Collectors.toList());

        return HistoryResponse.builder()
                .list(list)
                .total(result.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    @Override
    @Transactional
    public void deleteHistory(String deviceId, String id) {
        GenerationRecord record = generationMapper.selectById(id);
        if (record == null) {
            throw new BizException(40003, "记录不存在");
        }
        if (!record.getDeviceId().equals(deviceId)) {
            throw new BizException(40005, "无权操作此记录");
        }
        generationMapper.deleteById(id);
        favoriteMapper.delete(new LambdaQueryWrapper<FavoriteRecord>()
                .eq(FavoriteRecord::getGenerationId, id)
                .eq(FavoriteRecord::getDeviceId, deviceId));
    }

    @Override
    @Transactional
    public void clearHistory(String deviceId) {
        List<GenerationRecord> records = generationMapper.selectList(
                new LambdaQueryWrapper<GenerationRecord>()
                        .eq(GenerationRecord::getDeviceId, deviceId));

        List<String> ids = records.stream()
                .map(GenerationRecord::getId)
                .collect(Collectors.toList());

        if (!ids.isEmpty()) {
            generationMapper.deleteBatchIds(ids);
            favoriteMapper.delete(new LambdaQueryWrapper<FavoriteRecord>()
                    .in(FavoriteRecord::getGenerationId, ids)
                    .eq(FavoriteRecord::getDeviceId, deviceId));
        }
    }

    private HistoryResponse.HistoryItem toHistoryItem(GenerationRecord record) {
        StyleEnum styleEnum = StyleEnum.fromCode(record.getStyle());
        String contentPreview = record.getContent();
        if (contentPreview.length() > previewLength) {
            contentPreview = contentPreview.substring(0, previewLength) + "...";
        }

        return HistoryResponse.HistoryItem.builder()
                .id(record.getId())
                .scene(record.getScene())
                .style(record.getStyle())
                .styleName(styleEnum != null ? styleEnum.getName() : record.getStyle())
                .contentPreview(contentPreview)
                .favorited(record.getFavorited() == 1)
                .createdAt(record.getCreatedAt().toString() + "Z")
                .build();
    }
}
