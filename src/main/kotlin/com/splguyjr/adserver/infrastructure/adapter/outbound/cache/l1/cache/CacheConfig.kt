package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

// Spring Local Cache - Caffeine 사용
@Configuration
class CacheConfig {
    @Bean
    fun caffeineCacheManager(): CacheManager =
        CaffeineCacheManager(CacheNames.TOTAL, CacheNames.DAILY).apply {
            setCaffeine(
                Caffeine.newBuilder()
                    .maximumSize(50_000)
                    .expireAfterWrite(Duration.ofSeconds(5))
            )
        }
}