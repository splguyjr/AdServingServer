// application/scheduler/BudgetUpdateScheduler.kt
package com.splguyjr.adserver.application.scheduler

import com.splguyjr.adserver.application.port.inbound.BudgetUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BudgetUpdateScheduler(
    private val useCase: BudgetUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 매일 00:00:00 */
    @Scheduled(cron = "0 0 0 * * *")
    fun resetDailySpentAtMidnight() {
        val n = useCase.resetDailySpentForAll()
        log.info("자정 롤업 완료. processed={}", n)
    }
}
