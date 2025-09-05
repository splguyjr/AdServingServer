package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetReaderPort
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisScheduleCacheAdapter(
    private val stringRedis: StringRedisTemplate,
    private val spentTemplate: RedisTemplate<String, SpentBudget>
) : CandidateCachePort, SpentBudgetReaderPort {

    // --- CandidateCachePort ---
    override fun getCurrentCandidateScheduleIds(): Set<Long> =
        stringRedis.opsForSet().members(RedisKeys.candidateSchedules)
            ?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()

    /** Full rebuild: 기존 set 삭제 후 새 후보군으로 교체 */
    override fun overwriteCandidateScheduleIds(newIds: Set<Long>) {
        val key = RedisKeys.candidateSchedules
        stringRedis.delete(key)
        if (newIds.isNotEmpty()) {
            stringRedis.opsForSet().add(key, *newIds.map(Long::toString).toTypedArray())
        }
    }

    // --- SpentBudgetReaderPort ---
    override fun get(scheduleId: Long): SpentBudget? =
        spentTemplate.opsForValue().get(RedisKeys.scheduleSpentBudget(scheduleId))
}