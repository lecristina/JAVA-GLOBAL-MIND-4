package com.nexus.modules.cache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final CacheManager cacheManager;

    public void limparCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("🗑️ Cache '{}' limpo com sucesso", cacheName);
        } else {
            log.warn("⚠️ Cache '{}' não encontrado", cacheName);
        }
    }

    public void limparTodosCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("🗑️ Cache '{}' limpo", cacheName);
            }
        });
        log.info("✅ Todos os caches foram limpos");
    }
}


