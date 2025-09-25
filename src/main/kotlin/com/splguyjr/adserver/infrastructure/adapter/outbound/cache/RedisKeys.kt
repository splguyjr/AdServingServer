package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RedisKeys(
    @Value("\${cache.version:v0}")
    private val version: String
) {
    fun schedule(id: Long)           = "$version:schedule:{sch:$id}"
    fun scheduleSpentBudget(id: Long)= "$version:spent:{sch:$id}"
    fun candidateSchedules()         = "$version:candidate:schedules"
}