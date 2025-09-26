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

    /** 키가 없을 때만 초기화 */
    override fun initTotalIfAbsent(scheduleId: Long) {
        stringRedis.opsForValue().setIfAbsent(keys.scheduleTotalSpent(scheduleId), "0")
    }

    override fun incrTotal(scheduleId: Long, delta: Long): Long =
        stringRedis.opsForValue().increment(keys.scheduleTotalSpent(scheduleId), delta) ?: 0L
}
