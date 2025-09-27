package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.invalidation

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

/** L2 변경 후 → 분산 무효화 이벤트 발행(PUB) */
@Component
class CacheEventPublisher(
    private val redis: StringRedisTemplate,
    private val topics: RedisTopics
) {
    fun publishTotalEvict(scheduleId: Long) =
        redis.convertAndSend(topics.evictTotal(), scheduleId.toString())

    fun publishDailyEvict(scheduleId: Long) =
        redis.convertAndSend(topics.evictDaily(), scheduleId.toString())
}