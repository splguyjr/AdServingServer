package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetReaderPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.ScheduleRedisCache
import com.splguyjr.adserver.presentation.dto.AdDeliveryResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AdDeliveryService(
    private val candidateCache: CandidateCachePort,
    private val scheduleCache: ScheduleRedisCache,     // Redis 본문: scheduleId -> Schedule
    private val scheduleRepository: ScheduleRepository,
    private val spentBudgetReader: SpentBudgetReaderPort
) {
    @Transactional(readOnly = true)
    fun deliverOne(): AdDeliveryResponse? {
        val today = LocalDate.now()

        // 1) Redis 캐시 후보군들 중 하나 선정
        candidateCache.getCurrentCandidateScheduleIds()
            .shuffled()
            .forEach { id ->
                val schedule = scheduleCache.get(id) ?: scheduleRepository.findById(id)
                if (schedule != null) {
                    val spent = spentBudgetReader.get(id)
                    if (schedule.isEligibleToday(today, spent)) {
                        return AdDeliveryResponse.from(id, schedule, spent)
                    }
                }
            }

        // 2) fallback: 오늘자 eligible 계산 → 소진액 기준으로 필터 → 랜덤 선택
        val rows = scheduleRepository.findEligibleOnDateWithBudgets(today)

        val eligibleIds = rows.mapNotNull { r ->
            val spent = spentBudgetReader.get(r.scheduleId)
            val schedule = scheduleCache.get(r.scheduleId) ?: scheduleRepository.findById(r.scheduleId)

            if (schedule != null && schedule.isEligibleToday(today, spent)) r.scheduleId
            else null
        }

        if (eligibleIds.isEmpty()) return null

        val pick = eligibleIds.random()
        val schedule = scheduleCache.get(pick) ?: scheduleRepository.findById(pick) ?: return null
        val spent = spentBudgetReader.get(pick)

        return AdDeliveryResponse.from(pick, schedule, spent)
    }
}