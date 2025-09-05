package com.splguyjr.adserver.application.scheduler

import com.splguyjr.adserver.application.port.inbound.CandidateWarmupUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CandidateWarmupScheduler(
    private val useCase: CandidateWarmupUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 5분 주기 후보군 캐싱 */
    @Scheduled(cron = "0 */5 * * * *")
    fun warmup() {
        runCatching { useCase.warmup() }
            .onSuccess { r -> log.info("Warmup done. candidates={}", r.candidates) }
            .onFailure { e -> log.error("Warmup failed", e) }
    }
}
