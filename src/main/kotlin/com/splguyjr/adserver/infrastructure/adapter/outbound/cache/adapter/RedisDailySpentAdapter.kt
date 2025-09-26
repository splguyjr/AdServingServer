package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.adapter

import com.splguyjr.adserver.domain.port.outbound.cache.DailySpentPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisKeys
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisDailySpentAdapter(
    private val stringRedis: StringRedisTemplate,
    private val keys: RedisKeys
) : DailySpentPort {

    override fun getDaily(scheduleId: Long): Long? =
        stringRedis.opsForValue().get(keys.scheduleDailySpent(scheduleId))?.toLongOrNull()

    override fun putDaily(scheduleId: Long, value: Long) {
        stringRedis.opsForValue().set(keys.scheduleDailySpent(scheduleId), value.toString())
    }
}
