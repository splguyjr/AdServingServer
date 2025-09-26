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

    /** 키가 없을 때만 초기화 */
    override fun initDailyIfAbsent(scheduleId: Long) {
        stringRedis.opsForValue().setIfAbsent(keys.scheduleDailySpent(scheduleId), "0")
    }

    override fun incrDaily(scheduleId: Long, delta: Long): Long =
        stringRedis.opsForValue().increment(keys.scheduleDailySpent(scheduleId), delta) ?: 0L

    // INCRBY key 0는 값을 바꾸지 않는 no-op
    // 리셋은 0으로 교체가 필요하므로 GETSET
    override fun resetDailyToZero(scheduleId: Long): Long =
        stringRedis.opsForValue()
            .getAndSet(keys.scheduleDailySpent(scheduleId), "0")
            ?.toLongOrNull() ?: 0L
}
