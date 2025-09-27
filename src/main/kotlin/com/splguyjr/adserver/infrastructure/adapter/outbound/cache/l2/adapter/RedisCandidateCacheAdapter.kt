package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l2.adapter

import com.splguyjr.adserver.domain.port.outbound.cache.CandidateCachePort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l2.RedisKeys
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCandidateCacheAdapter(
    private val stringRedis: StringRedisTemplate,
    private val key: RedisKeys
) : CandidateCachePort {

    override fun getCurrentCandidateScheduleIds(): Set<Long> =
        stringRedis.opsForSet().members(key.candidateSchedules())
            ?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()

    override fun overwriteCandidateScheduleIds(newIds: Set<Long>) {
        val redisKey = key.candidateSchedules()
        stringRedis.delete(redisKey)
        if (newIds.isNotEmpty()) {
            stringRedis.opsForSet().add(redisKey, *newIds.map(Long::toString).toTypedArray())
        }
    }
}