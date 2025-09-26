package com.splguyjr.adserver.domain.port.outbound.cache

import com.splguyjr.adserver.domain.model.Schedule

interface ScheduleCachePort {
    fun get(scheduleId: Long): Schedule?
    fun put(scheduleId: Long, schedule: Schedule)
    fun delete(scheduleId: Long)
}
