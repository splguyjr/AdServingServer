package com.splguyjr.adserver.application.scheduler

import com.splguyjr.adserver.application.port.inbound.ScheduleSyncUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SyncScheduler(
    private val useCase: ScheduleSyncUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 20초 주기 자동 호출 */
    @Scheduled(fixedDelay = 20000)
    fun syncAll() {
        try {
            val n = useCase.sync()
            log.info("SyncScheduler done. upserted={}", n)
        } catch (e: Exception) {
            log.error("SyncScheduler failed", e)
        }
    }
}