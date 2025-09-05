package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

// Redis 키 네이밍 전용 유틸리티 클래스
object RedisKeys {
    fun schedule(scheduleId: Long) = "${scheduleId}:Schedule"
    fun scheduleSpentBudget(scheduleId: Long) = "${scheduleId}:SpentBudget"
}