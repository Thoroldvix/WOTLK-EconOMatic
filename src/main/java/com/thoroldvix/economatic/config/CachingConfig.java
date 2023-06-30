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

    @Bean
    CacheManager itemCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList(("item-cache")));
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(20000)
                .expireAfterWrite(5, TimeUnit.MINUTES);
        cacheManager.setCaffeine(cacheBuilder);
        return cacheManager;
    }

    @Bean
    CacheManager itemPriceCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList(("item-price-cache")));
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(30000)
                .expireAfterWrite(60, TimeUnit.SECONDS);
        cacheManager.setCaffeine(cacheBuilder);
        return cacheManager;
    }

    @Bean
    CacheManager populationCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList(("population-cache")));
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(30000)
                .expireAfterWrite(60, TimeUnit.SECONDS);
        cacheManager.setCaffeine(cacheBuilder);
        return cacheManager;
    }

    @Bean
    CacheManager goldPriceCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList(("gold-price-cache")));
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .initialCapacity(96)
                .maximumSize(96)
                .expireAfterWrite(60, TimeUnit.SECONDS);
        cacheManager.setCaffeine(cacheBuilder);
        return cacheManager;
    }

    @Bean
    CacheManager serverCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList(("server-cache")));
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .initialCapacity(96)
                .maximumSize(96)
                .expireAfterWrite(5, TimeUnit.MINUTES);
        cacheManager.setCaffeine(cacheBuilder);
        return cacheManager;
    }

    @Primary
    @Bean
    public CacheManager cacheManagerComposite() {
        CompositeCacheManager cacheManager = new CompositeCacheManager();
        cacheManager.setCacheManagers(Arrays.asList(serverCache(), itemCache(), itemPriceCache(), goldPriceCache(), populationCache()));

        return cacheManager;
    }
}
