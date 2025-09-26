package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RedisKeys(
    @Value("\${cache.version:v0}")
    private val version: String
) {
    fun schedule(id: Long)           = "$version:schedule:{sch:$id}"

    fun scheduleTotalSpent(id: Long)       = "$version:spent:total:{sch:$id}"
    fun scheduleDailySpent(id: Long)       = "$version:spent:daily:{sch:$id}"

    fun candidateSchedules()         = "$version:candidate:schedules"
}