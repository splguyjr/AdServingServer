package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.BudgetUpdateUseCase
import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetPort
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BudgetUpdateService (
    private val candidateCachePort: CandidateCachePort,
    private val spentBudgetPort: SpentBudgetPort
) : BudgetUpdateUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun rollDailyIntoTotalForAll(): Int {
        val ids = candidateCachePort.getCurrentCandidateScheduleIds()
        var processed = 0

        ids.forEach { id ->
            val currentBudget = spentBudgetPort.get(id) ?: return@forEach   // null 이면 건너뜀

            val updateBudget = SpentBudget(
                totalSpentBudget = currentBudget.totalSpentBudget + currentBudget.dailySpentBudget,
                dailySpentBudget = 0L
            )
            spentBudgetPort.put(id, updateBudget)
            processed++
        }

        log.info("BudgetUpdate: 일일 소진액 → 누적 소진액 반영 완료. 처리 건수={}", processed)
        return processed
    }

}