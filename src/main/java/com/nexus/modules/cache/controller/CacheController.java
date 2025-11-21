package com.nexus.modules.cache.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.nexus.modules.cache.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// DESABILITADO: Endpoint de cache removido
/*
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
@Tag(name = "Cache", description = "Gerenciamento e demonstração de cache (Caffeine)")
@SecurityRequirement(name = "bearerAuth")
public class CacheController {

    private final CacheService cacheService;
    private final CacheManager cacheManager;

    @GetMapping("/stats")
    @Operation(
        summary = "Obter estatísticas do cache",
        description = "Retorna estatísticas detalhadas de todos os caches (hit rate, miss rate, tamanho, etc.) " +
                     "para demonstrar o funcionamento do sistema de cache."
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (cache != null) {
                CacheStats cacheStats = cache.getNativeCache().stats();
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("hitCount", cacheStats.hitCount());
                cacheInfo.put("missCount", cacheStats.missCount());
                cacheInfo.put("hitRate", String.format("%.2f%%", cacheStats.hitRate() * 100));
                cacheInfo.put("missRate", String.format("%.2f%%", cacheStats.missRate() * 100));
                cacheInfo.put("requestCount", cacheStats.requestCount());
                cacheInfo.put("evictionCount", cacheStats.evictionCount());
                cacheInfo.put("averageLoadPenalty", cacheStats.averageLoadPenalty());
                cacheInfo.put("size", cache.getNativeCache().estimatedSize());
                stats.put(cacheName, cacheInfo);
            }
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("caches", stats);
        response.put("totalCaches", cacheManager.getCacheNames().size());
        response.put("message", "Cache está ativo e funcionando! Use os endpoints de listagem para ver o cache em ação.");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear/{cacheName}")
    @Operation(
        summary = "Limpar cache específico",
        description = "Limpa um cache específico para demonstrar a invalidação de cache."
    )
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Map<String, String>> limparCache(@PathVariable String cacheName) {
        cacheService.limparCache(cacheName);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache '" + cacheName + "' limpo com sucesso!");
        response.put("cacheName", cacheName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear-all")
    @Operation(
        summary = "Limpar todos os caches",
        description = "Limpa todos os caches para demonstrar a invalidação completa."
    )
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Map<String, String>> limparTodosCaches() {
        cacheService.limparTodosCaches();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Todos os caches foram limpos com sucesso!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @Operation(
        summary = "Testar cache",
        description = "Endpoint de teste para demonstrar o funcionamento do cache. " +
                     "Chame este endpoint múltiplas vezes para ver o cache em ação."
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Map<String, Object>> testarCache() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cache está funcionando! Verifique os logs para ver '🔍 Buscando...' e '✅ Dados retornados do cache'.");
        response.put("instruction", "Chame endpoints como GET /api/badges múltiplas vezes para ver o cache em ação. " +
                                   "A primeira chamada buscará do banco, as próximas virão do cache.");
        response.put("endpoints", new String[]{
            "GET /api/badges - Listar badges (cacheável)",
            "GET /api/humor/usuario/{id} - Listar humor (cacheável)",
            "GET /api/habitos/usuario/{id} - Listar hábitos (cacheável)",
            "GET /api/sprints/usuario/{id} - Listar sprints (cacheável)"
        });
        return ResponseEntity.ok(response);
    }
}
*/
