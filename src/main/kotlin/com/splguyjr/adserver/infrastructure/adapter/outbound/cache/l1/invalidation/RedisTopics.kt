package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.invalidation

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/** Redis Pub/Sub '토픽(채널)' 네임스페이스 */
@Component
class RedisTopics(
    @Value("\${cache.version:v0}") private val version: String
) {
    fun evictTotal() = "$version:topic:evict:spent:total"
    fun evictDaily() = "$version:topic:evict:spent:daily"
}