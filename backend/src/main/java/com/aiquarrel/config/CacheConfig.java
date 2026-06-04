package com.aiquarrel.config;

import com.aiquarrel.model.dto.GenerateResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, AtomicInteger> dailyCountCache() {
        return Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10_000)
                .expireAfterWrite(48, TimeUnit.HOURS)
                .expireAfterAccess(24, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    @Bean
    public Cache<String, AtomicInteger> rateLimitCache() {
        return Caffeine.newBuilder()
                .initialCapacity(500)
                .maximumSize(5_000)
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public Cache<String, GenerateResponse> genRecordCache() {
        return Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(1_000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public Cache<String, Integer> sensitiveWordsCache() {
        return Caffeine.newBuilder()
                .initialCapacity(5000)
                .maximumSize(10_000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }
}
