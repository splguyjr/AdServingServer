package com.splguyjr.adserver.domain.port.outbound.cache

interface DailySpentPort {
    fun getDaily(scheduleId: Long): Long?
    fun initDailyIfAbsent(scheduleId: Long)
    fun incrDaily(scheduleId: Long, delta: Long): Long
    fun resetDailyToZero(scheduleId: Long): Long
}
