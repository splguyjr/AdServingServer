package com.splguyjr.adserver.domain.port.outbound.cache

interface DailySpentPort {
    fun getDaily(scheduleId: Long): Long?
    fun putDaily(scheduleId: Long, value: Long)
}