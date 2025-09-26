// application/scheduler/BudgetUpdateScheduler.kt
package com.splguyjr.adserver.application.scheduler

import com.splguyjr.adserver.application.port.inbound.BudgetUpdateUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BudgetUpdateScheduler(
    private val useCase: BudgetUpdateUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 매일 00:00:00 */
    @Scheduled(cron = "0 0 0 * * *")
    fun midnightRollup() {
        val n = useCase.rollDailyIntoTotalForAll()
        log.info("자정 롤업 완료. processed={}", n)
    }
}
