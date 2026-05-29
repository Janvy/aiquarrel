package com.aiquarrel.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private Cache<String, Boolean> caffeineCache;

    private static final String REDIS_KEY_LEVEL1 = "sensitive:words:level1";
    private static final String REDIS_KEY_LEVEL2 = "sensitive:words:level2";

    @PostConstruct
    public void init() {
        caffeineCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    public boolean isSafe(String text) {
        return !containsLevel1Word(text);
    }

    public String filter(String text) {
        if (text == null) return null;
        String result = text;
        if (containsLevel1Word(result)) {
            return null;
        }
        result = replaceLevel2Words(result);
        return result;
    }

    private boolean containsLevel1Word(String text) {
        Set<Object> level1Words = redisTemplate.opsForSet().members(REDIS_KEY_LEVEL1);
        if (level1Words == null || level1Words.isEmpty()) return false;

        for (Object word : level1Words) {
            String keyword = word.toString();
            Boolean cached = caffeineCache.getIfPresent("L1:" + keyword + ":" + text.hashCode());
            if (cached != null) return cached;

            if (text.contains(keyword)) {
                caffeineCache.put("L1:" + keyword + ":" + text.hashCode(), true);
                log.warn("内容命中Level1敏感词: {}", keyword);
                return true;
            }
        }
        return false;
    }

    private String replaceLevel2Words(String text) {
        Set<Object> level2Words = redisTemplate.opsForSet().members(REDIS_KEY_LEVEL2);
        if (level2Words == null || level2Words.isEmpty()) return text;

        String result = text;
        for (Object word : level2Words) {
            String keyword = word.toString();
            if (result.contains(keyword)) {
                result = result.replace(keyword, "***");
                log.warn("内容命中Level2敏感词: {}", keyword);
            }
        }
        return result;
    }
}
