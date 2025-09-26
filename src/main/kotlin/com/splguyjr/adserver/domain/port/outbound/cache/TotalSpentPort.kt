package com.splguyjr.adserver.domain.port.outbound.cache

interface TotalSpentPort {
    fun getTotal(scheduleId: Long): Long?
    fun putTotal(scheduleId: Long, value: Long)
}