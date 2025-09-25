package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class SpentBudgetRedisCache(
    private val template: RedisTemplate<String, SpentBudget>,
    private val key: RedisKeys
) {
    fun put(scheduleId: Long, value: SpentBudget) {
        template.opsForValue().set(key.scheduleSpentBudget(scheduleId), value)
    }

    fun get(scheduleId: Long): SpentBudget? =
        template.opsForValue().get(key.scheduleSpentBudget(scheduleId))

    fun delete(scheduleId: Long) {
        template.delete(key.scheduleSpentBudget(scheduleId))
    }
}