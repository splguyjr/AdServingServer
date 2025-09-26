package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.BudgetUseCase
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.cache.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.cache.ScheduleCachePort
import com.splguyjr.adserver.presentation.dto.AdDeliveryResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AdDeliveryService(
    private val candidateCache: CandidateCachePort,
    private val scheduleCache: ScheduleCachePort,     // Redis 본문: scheduleId -> Schedule
    private val scheduleRepository: ScheduleRepository,
    private val budgetUseCase: BudgetUseCase
) {
    data class Pick(val id: Long, val schedule: Schedule)

    fun deliverOne(): AdDeliveryResponse? {
        val today = LocalDate.now()
        val pick = selectEligible(today) ?: return null

        // 과금 수행(내부에서 SpentBudget 조회/갱신) 후, 최신 예산으로 응답 구성
        val updatedBudget = budgetUseCase.applyCharge(pick.id, pick.schedule)
        return AdDeliveryResponse.from(pick.id, pick.schedule, updatedBudget)
    }

    fun selectEligible(today: LocalDate): Pick? {
        // 1) 후보군에서 순차 탐색
        for (id in candidateCache.getCurrentCandidateScheduleIds()) {
            val sch = scheduleCache.get(id) ?: scheduleRepository.findById(id) ?: continue
            if (budgetUseCase.isEligibleToday(id, sch, today)) {
                return Pick(id, sch)
            }
        }
        // 2) 폴백: DB 기반 후보 조회 후 순차 탐색
        for (r in scheduleRepository.findEligibleOnDateWithBudgets(today)) {
            val id = r.scheduleId
            val sch = scheduleCache.get(id) ?: scheduleRepository.findById(id) ?: continue
            if (budgetUseCase.isEligibleToday(id, sch, today)) {
                return Pick(id, sch)
            }
        }
        return null
    }
}