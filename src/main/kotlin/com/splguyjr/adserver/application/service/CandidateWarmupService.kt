package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.CandidateWarmupResult
import com.splguyjr.adserver.application.port.inbound.CandidateWarmupUseCase
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.cache.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.cache.DailySpentPort
import com.splguyjr.adserver.domain.port.outbound.cache.TotalSpentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CandidateWarmupService(
    private val scheduleRepo: ScheduleRepository,
    private val totalReader: TotalSpentPort,
    private val dailyReader: DailySpentPort,
    private val candidateCache: CandidateCachePort
) : CandidateWarmupUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun warmup(): CandidateWarmupResult {
        val today = LocalDate.now()

        // 1) DB: 상태/기간 만족 + 예산 상한
        val rows = scheduleRepo.findEligibleOnDateWithBudgets(today)

        // 2) 예산 조건(total, daily)을 만족하는 스케줄만 필터링
        val eligibleIds = rows.mapNotNull { r ->
            val total = totalReader.getTotal(r.scheduleId) ?: 0L
            val daily = dailyReader.getDaily(r.scheduleId) ?: 0L
            if (total < r.campaignTotalBudget && daily < r.adSetDailyBudget) r.scheduleId else null
        }.toSet()

        // 3) 후보군 전면 교체 + redis에 (candidate key : 후보군 키셋) 캐싱
        candidateCache.overwriteCandidateScheduleIds(eligibleIds)

        log.info("candidate warmup → candidates={}", eligibleIds.size)
        return CandidateWarmupResult(candidates = eligibleIds.size)
    }
}
