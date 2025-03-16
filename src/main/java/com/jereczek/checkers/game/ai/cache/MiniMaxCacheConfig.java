package com.jereczek.checkers.game.ai.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MiniMaxCacheConfig {
    @Bean
    public Cache<MiniMaxCacheKey, MiniMaxCacheEntry> miniMaxCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .recordStats()
                .build();
    }
}
