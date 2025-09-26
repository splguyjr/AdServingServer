package com.splguyjr.adserver.application.port.inbound

import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import java.time.LocalDate

interface BudgetUseCase {
    /** KST 자정 배치: 모든 후보 스케줄과 소진액에 대해 daily=0 */
    fun resetDailySpentForAll(): Int

    /** 오늘 서빙 가능 여부 판단 (내부에서 SpentBudget 조회) */
    fun isEligibleToday(scheduleId: Long, schedule: Schedule, today: LocalDate): Boolean

    /** 서빙 1회 등 과금 발생 시: daily/total 동시 증가 */
    fun applyCharge(scheduleId: Long, schedule: Schedule): SpentBudget
}