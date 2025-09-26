package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.CandidateWarmupResult
import com.splguyjr.adserver.application.port.inbound.CandidateWarmupUseCase
import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CandidateWarmupService(
    private val scheduleRepo: ScheduleRepository,
    private val budgetReader: SpentBudgetPort,
    private val candidateCache: CandidateCachePort
) : CandidateWarmupUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun warmup(): CandidateWarmupResult {
        val today = LocalDate.now()

        // 1) DB: 상태/기간 만족 + 예산 상한
        val rows = scheduleRepo.findEligibleOnDateWithBudgets(today)

        // 2. 예산 조건(total, daily)을 만족하는 스케줄만 필터링
        val eligibleIds = rows.mapNotNull { r ->
            val b = budgetReader.get(r.scheduleId)
            val total = b?.totalSpentBudget ?: 0L
            val daily = b?.dailySpentBudget ?: 0L
            if (total < r.campaignTotalBudget && daily < r.adSetDailyBudget) r.scheduleId else null
        }.toSet()

        // 2) 후보군 전면 교체
        candidateCache.overwriteCandidateScheduleIds(eligibleIds)

        log.info("candidate warmup → candidates={}", eligibleIds.size)
        return CandidateWarmupResult(candidates = eligibleIds.size)
    }
}