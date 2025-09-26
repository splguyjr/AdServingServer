package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.BudgetUseCase
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.cache.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.cache.DailySpentPort
import com.splguyjr.adserver.domain.port.outbound.cache.ScheduleCachePort
import com.splguyjr.adserver.domain.port.outbound.cache.TotalSpentPort
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BudgetService (
    private val candidateCachePort: CandidateCachePort,
    private val totalPort: TotalSpentPort,
    private val dailyPort: DailySpentPort,
    private val scheduleCache: ScheduleCachePort,
) : BudgetUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    /** 자정 배치: 일일 소진액만 0으로 리셋 */
    override fun resetDailySpentForAll(): Int {
        val ids = candidateCachePort.getCurrentCandidateScheduleIds()

        val resetBudgetCount = resetDailySpentBudgets(ids)
        val resetScheduleCount = resetCachedSchedulesDailySpent(ids)

        log.info(
            "BudgetUpdate: 일일 소진액 리셋 완료. budgets={}, schedules={}, candidates={}",
            resetBudgetCount, resetScheduleCount, ids.size
        )
        return resetBudgetCount
    }

    /** 일일 소진액만 0으로 리셋 */
    private fun resetDailySpentBudgets(ids: Set<Long>): Int {
        var count = 0
        ids.forEach { id ->
            val curDaily = dailyPort.getDaily(id) ?: 0L
            if (curDaily == 0L) return@forEach
            dailyPort.putDaily(id, 0L)
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
        val spent = SpentBudget(
            totalSpentBudget = totalPort.getTotal(scheduleId) ?: 0L,
            dailySpentBudget = dailyPort.getDaily(scheduleId) ?: 0L
        )
        return schedule.isEligibleToday(today, spent)
    }

    /** total/daily를 각각 더하고 최신값 반환 */
    override fun applyCharge(scheduleId: Long, schedule: Schedule): SpentBudget {
        val delta = schedule.adSet.bidAmount

        val baseTotal = totalPort.getTotal(scheduleId) ?: 0L
        val baseDaily = dailyPort.getDaily(scheduleId) ?: 0L

        val updatedTotal = baseTotal + delta
        val updatedDaily = baseDaily + delta

        totalPort.putTotal(scheduleId, updatedTotal)
        dailyPort.putDaily(scheduleId, updatedDaily)

        return SpentBudget(
            totalSpentBudget = updatedTotal,
            dailySpentBudget = updatedDaily
        )
    }
}
