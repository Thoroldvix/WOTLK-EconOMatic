package com.thoroldvix.economatic.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {

    @Primary
    @Bean
    public CacheManager cacheManagerComposite() {
        CompositeCacheManager cacheManager = new CompositeCacheManager();
        cacheManager.setCacheManagers(Arrays.asList(
                serverCache(),
                itemCache(),
                itemPriceCache(),
                goldPriceCache(),
                populationCache(),
                goldPriceStatsCache()));
        return cacheManager;
    }

    @Bean
    CacheManager itemCache() {
        return createCacheManager("item-cache", 100, 20000, 5, TimeUnit.MINUTES);
    }

    @Bean
    CacheManager goldPriceStatsCache() {
        return createCacheManager("gold-price-stats-cache", 100, 20000, 60, TimeUnit.SECONDS);
    }

    @Bean
    CacheManager itemPriceCache() {
        return createCacheManager("item-price-cache", 100, 30000, 60, TimeUnit.SECONDS);
    }

    @Bean
    CacheManager populationCache() {
        return createCacheManager("population-cache", 100, 30000, 60, TimeUnit.SECONDS);
    }

    @Bean
    CacheManager goldPriceCache() {
        return createCacheManager("gold-price-cache", 96, 96, 60, TimeUnit.SECONDS);
    }

    @Bean
    CacheManager serverCache() {
        return createCacheManager("server-cache", 96, 96, 5, TimeUnit.MINUTES);
    }

    private CacheManager createCacheManager(String name, int initialCapacity, int maxSize, long duration, TimeUnit timeUnit) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList(name));
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maxSize)
                .expireAfterWrite(duration, timeUnit);
        cacheManager.setCaffeine(cacheBuilder);
        return cacheManager;
    }
}