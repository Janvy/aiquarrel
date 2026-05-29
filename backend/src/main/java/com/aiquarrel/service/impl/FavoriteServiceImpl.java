package com.aiquarrel.service.impl;

import com.aiquarrel.exception.BizException;
import com.aiquarrel.model.dto.FavoriteResponse;
import com.aiquarrel.model.dto.HistoryResponse;
import com.aiquarrel.model.entity.FavoriteRecord;
import com.aiquarrel.model.entity.GenerationRecord;
import com.aiquarrel.model.enums.StyleEnum;
import com.aiquarrel.model.mapper.FavoriteMapper;
import com.aiquarrel.model.mapper.GenerationMapper;
import com.aiquarrel.service.FavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
public class FavoriteServiceImpl implements FavoriteService {

    private final GenerationMapper generationMapper;
    private final FavoriteMapper favoriteMapper;

    @Value("${app.content.content-preview-length:30}")
    private int previewLength;

    @Override
    @Transactional
    public FavoriteResponse toggleFavorite(String deviceId, String generationId) {
        GenerationRecord record = generationMapper.selectById(generationId);
        if (record == null) {
            throw new BizException(40003, "记录不存在");
        }
        if (!record.getDeviceId().equals(deviceId)) {
            throw new BizException(40005, "无权操作此记录");
        }

        boolean newFavorited = record.getFavorited() != 1;

        // 双写：更新 t_generation_record.favorited
        generationMapper.update(null, new LambdaUpdateWrapper<GenerationRecord>()
                .eq(GenerationRecord::getId, generationId)
                .set(GenerationRecord::getFavorited, newFavorited ? 1 : 0));

        // 双写：维护 t_favorite
        if (newFavorited) {
            FavoriteRecord favorite = new FavoriteRecord();
            favorite.setDeviceId(deviceId);
            favorite.setGenerationId(generationId);
            favoriteMapper.insert(favorite);
        } else {
            favoriteMapper.delete(new LambdaQueryWrapper<FavoriteRecord>()
                    .eq(FavoriteRecord::getDeviceId, deviceId)
                    .eq(FavoriteRecord::getGenerationId, generationId));
        }

        return FavoriteResponse.builder()
                .id(generationId)
                .favorited(newFavorited)
                .build();
    }

    @Override
    public HistoryResponse getFavorites(String deviceId, int page, int pageSize) {
        Page<GenerationRecord> pageReq = new Page<>(page, pageSize);
        LambdaQueryWrapper<GenerationRecord> wrapper = new LambdaQueryWrapper<GenerationRecord>()
                .eq(GenerationRecord::getDeviceId, deviceId)
                .eq(GenerationRecord::getFavorited, 1)
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
                .favorited(true)
                .createdAt(record.getCreatedAt().toString() + "Z")
                .build();
    }
}
