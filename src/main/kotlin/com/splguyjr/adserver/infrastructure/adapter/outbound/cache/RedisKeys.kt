package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

// Redis 키 네이밍 전용 유틸리티 클래스
object RedisKeys {
    /** 스케줄 본문 캐시 (Value) */
    fun schedule(scheduleId: Long) = "${scheduleId}:Schedule"

    /** 스케줄 소진액 캐시 (Value) - total/daily 포함 */
    fun scheduleSpentBudget(scheduleId: Long) = "${scheduleId}:SpentBudget"

    /** 후보 스케줄 ID 집합 (Set) */
    const val candidateSchedules: String = "cand:schedules"
}