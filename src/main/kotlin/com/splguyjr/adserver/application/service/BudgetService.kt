package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.BudgetUseCase
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetPort
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.ScheduleRedisCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BudgetService (
    private val candidateCachePort: CandidateCachePort,
    private val spentBudgetPort: SpentBudgetPort,
    private val scheduleCache: ScheduleRedisCache,
) : BudgetUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    /** 자정 배치: daily만 0으로 리셋 (total은 그대로 유지) */
    override fun resetDailySpentForAll(): Int {
        val ids = candidateCachePort.getCurrentCandidateScheduleIds()

        val resetBudgetCount = resetDailySpentBudgets(ids)
        val resetScheduleCount = resetCachedSchedulesDailySpent(ids)

        log.info(
            "BudgetUpdate: 일일 소진액 리셋 완료. budgets={}, schedules={}, candidates={}",
            resetBudgetCount, resetScheduleCount, ids.size
        )
        // 반환값은 핵심 지표(예: budget 리셋 건수)로 유지
        return resetBudgetCount
    }

    /** SpentBudgetPort 기준으로 daily만 0으로 리셋 */
    private fun resetDailySpentBudgets(ids: Set<Long>): Int {
        var count = 0
        ids.forEach { id ->
            val cur = spentBudgetPort.get(id) ?: return@forEach
            if (cur.dailySpentBudget == 0L) return@forEach

            val reset = SpentBudget(
                totalSpentBudget = cur.totalSpentBudget, // total은 유지
                dailySpentBudget = 0L
            )
            spentBudgetPort.put(id, reset)
            count++
        }
        return count
    }

    /** 후보군에 캐시된 Schedule 객체 내부의 adSet.dailySpentBudget도 0으로 동기화 */
    private fun resetCachedSchedulesDailySpent(ids: Set<Long>): Int {
        var count = 0
        ids.forEach { id ->
            val sch = scheduleCache.get(id) ?: return@forEach
            if (sch.adSet.dailySpentBudget == 0L) return@forEach

            val updated = sch.copy(adSet = sch.adSet.copy(dailySpentBudget = 0L))
            scheduleCache.put(id, updated)
            count++
        }
        return count
    }

    /** 오늘 서빙 가능 여부 */
    override fun isEligibleToday(scheduleId: Long, schedule: Schedule, today: LocalDate): Boolean {
        val spent = spentBudgetPort.get(scheduleId)
        return schedule.isEligibleToday(today, spent)
    }

    override fun applyCharge(scheduleId: Long, schedule: Schedule): SpentBudget {
        val delta = schedule.adSet.bidAmount
        val base = spentBudgetPort.get(scheduleId) ?: SpentBudget(0L, 0L)
        val updated = SpentBudget(
            totalSpentBudget = base.totalSpentBudget + delta,
            dailySpentBudget = base.dailySpentBudget + delta
        )
        spentBudgetPort.put(scheduleId, updated)
        return updated
    }

}