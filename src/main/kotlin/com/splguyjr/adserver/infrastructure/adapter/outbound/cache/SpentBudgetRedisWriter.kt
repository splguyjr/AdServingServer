package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.model.SpentBudget
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class SpentBudgetRedisWriter(
    private val spentBudgetRedisTemplate: RedisTemplate<String, SpentBudget>
) {
    fun put(scheduleId: Long, value: SpentBudget) {
        val key = RedisKeys.scheduleSpentBudget(scheduleId)
        spentBudgetRedisTemplate.opsForValue().set(key, value)
    }

    fun get(scheduleId: Long): SpentBudget? =
        spentBudgetRedisTemplate.opsForValue().get(RedisKeys.scheduleSpentBudget(scheduleId))

    fun delete(scheduleId: Long) {
        spentBudgetRedisTemplate.delete(RedisKeys.scheduleSpentBudget(scheduleId))
    }
}