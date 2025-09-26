package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.adapter

import com.splguyjr.adserver.domain.port.outbound.cache.TotalSpentPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisKeys
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisTotalSpentAdapter(
    private val stringRedis: StringRedisTemplate,
    private val keys: RedisKeys
) : TotalSpentPort {

    override fun getTotal(scheduleId: Long): Long? =
        stringRedis.opsForValue().get(keys.scheduleTotalSpent(scheduleId))?.toLongOrNull()

    override fun putTotal(scheduleId: Long, value: Long) {
        stringRedis.opsForValue().set(keys.scheduleTotalSpent(scheduleId), value.toString())
    }
}
