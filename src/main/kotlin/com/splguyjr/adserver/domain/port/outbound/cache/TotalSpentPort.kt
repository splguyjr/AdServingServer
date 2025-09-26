package com.splguyjr.adserver.domain.port.outbound.cache

interface TotalSpentPort {
    fun getTotal(scheduleId: Long): Long?
    fun initTotalIfAbsent(scheduleId: Long)
    fun incrTotal(scheduleId: Long, delta: Long): Long
}