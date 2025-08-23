package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto.CreativeCache
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCacheWriter(
    private val stringRedis: StringRedisTemplate,                   // cand:adsets (Set)
    private val creativeRedis: RedisTemplate<String, CreativeCache> // cand:adset:{id} (값=CreativeCache)
) {
    /** cand:adsets 전량 교체 + TTL */
    fun overwriteCandidateSet(key: String, members: Collection<String>) {
        val ops = stringRedis.opsForSet()
        stringRedis.delete(key)
        if (members.isNotEmpty()) {
            ops.add(key, *members.toTypedArray())
            stringRedis.expire(key, RedisTtl.default)
        }
    }

    /** cand:adset:{adSetId} ← CreativeCache 저장 + TTL */
    fun putCreative(adSetId: Long, value: CreativeCache) {
        creativeRedis.opsForValue().set(RedisKeys.adsetCreative(adSetId), value, RedisTtl.default)
    }

    /** cand:adset:{adSetId} 읽기 */
    fun getCreative(adSetId: Long): CreativeCache? =
        creativeRedis.opsForValue().get(RedisKeys.adsetCreative(adSetId))

    /** cand:adset:{adSetId} 삭제 */
    fun deleteCreative(adSetId: Long) {
        creativeRedis.delete(RedisKeys.adsetCreative(adSetId))
    }

    /** cand:adsets에서 랜덤 adSetId 문자열 하나 */
    fun srandAdSet(): String? =
        stringRedis.opsForSet().randomMember(RedisKeys.candidateAdSets)
}