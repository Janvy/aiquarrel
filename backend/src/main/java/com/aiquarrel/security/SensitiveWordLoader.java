package com.aiquarrel.security;

import com.aiquarrel.model.entity.SensitiveWord;
import com.aiquarrel.model.mapper.SensitiveWordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordLoader {

    private final SensitiveWordMapper sensitiveWordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_LEVEL1 = "sensitive:words:level1";
    private static final String REDIS_KEY_LEVEL2 = "sensitive:words:level2";

    @PostConstruct
    public void loadSensitiveWords() {
        log.info("开始加载敏感词库...");
        List<SensitiveWord> words = sensitiveWordMapper.selectList(new LambdaQueryWrapper<>());

        Set<String> level1 = words.stream()
                .filter(w -> w.getLevel() == 1)
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());

        Set<String> level2 = words.stream()
                .filter(w -> w.getLevel() == 2)
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());

        redisTemplate.delete(REDIS_KEY_LEVEL1);
        redisTemplate.delete(REDIS_KEY_LEVEL2);

        if (!level1.isEmpty()) {
            redisTemplate.opsForSet().add(REDIS_KEY_LEVEL1, level1.toArray());
        }
        if (!level2.isEmpty()) {
            redisTemplate.opsForSet().add(REDIS_KEY_LEVEL2, level2.toArray());
        }

        log.info("敏感词库加载完成: Level1={}个, Level2={}个", level1.size(), level2.size());
    }
}
