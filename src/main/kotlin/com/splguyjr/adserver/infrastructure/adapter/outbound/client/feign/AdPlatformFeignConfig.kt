package com.splguyjr.adserver.infrastructure.adapter.outbound.client.feign

import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdPlatformFeignConfig {
    /**
     * 재시도 정책 (지수 백오프)
     * - 초기 간격 200ms, 최대 간격 1000ms, 최대 5회 시도
     */
    @Bean
    fun feignRetryer(): Retryer =
        Retryer.Default(
            /* period */ 200L,
            /* maxPeriod */ 1000L,
            /* maxAttempts */ 5
        )
}
