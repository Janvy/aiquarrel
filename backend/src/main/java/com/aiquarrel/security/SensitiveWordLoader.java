package com.aiquarrel.security;

import com.aiquarrel.model.entity.SensitiveWord;
import com.aiquarrel.model.mapper.SensitiveWordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordLoader {

    private final SensitiveWordMapper sensitiveWordMapper;
    private final Cache<String, Integer> sensitiveWordsCache;

    @PostConstruct
    public void loadSensitiveWords() {
        log.info("开始从MySQL加载敏感词库到Caffeine...");
        List<SensitiveWord> words = sensitiveWordMapper.selectList(new LambdaQueryWrapper<>());

        int level1Count = 0;
        int level2Count = 0;
        for (SensitiveWord word : words) {
            sensitiveWordsCache.put(word.getWord(), word.getLevel());
            if (word.getLevel() == 1) {
                level1Count++;
            } else {
                level2Count++;
            }
        }

        log.info("敏感词库加载完成: 共{}条, Level1={}条, Level2={}条", words.size(), level1Count, level2Count);
    }
}
