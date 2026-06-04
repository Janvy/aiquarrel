package com.aiquarrel.security;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentFilter {

    private final Cache<String, Integer> sensitiveWordsCache;

    /** 检查文本是否安全（不含Level1敏感词） */
    public boolean isSafe(String text) {
        if (text == null) return true;
        for (String word : sensitiveWordsCache.asMap().keySet()) {
            Integer level = sensitiveWordsCache.getIfPresent(word);
            if (level != null && level == 1 && text.contains(word)) {
                log.warn("内容命中Level1敏感词: {}", word);
                return false;
            }
        }
        return true;
    }

    /**
     * 过滤文本：Level1 → 返回null（拦截），Level2 → 替换为***
     * @return 过滤后的文本，命中Level1返回null
     */
    public String filter(String text) {
        if (text == null) return null;
        String result = text;

        for (var entry : sensitiveWordsCache.asMap().entrySet()) {
            String word = entry.getKey();
            Integer level = entry.getValue();

            if (level == null) continue;

            if (level == 1 && result.contains(word)) {
                log.warn("内容命中Level1敏感词，拦截: {}", word);
                return null;
            }
            if (level == 2 && result.contains(word)) {
                result = result.replace(word, "***");
                log.warn("内容命中Level2敏感词，替换: {}", word);
            }
        }
        return result;
    }
}
