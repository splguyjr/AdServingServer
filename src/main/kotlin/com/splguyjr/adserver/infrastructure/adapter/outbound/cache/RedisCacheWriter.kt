package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import org.springframework.data.redis.core.SessionCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisCacheWriter(
    private val redis: StringRedisTemplate
) {
    fun overwriteSet(key: String, members: Collection<String>, ttl: Duration = RedisTtl.default) {
        redis.executePipelined(SessionCallback<Any> { ops ->
            ops.delete(key)
            if (members.isNotEmpty()) ops.opsForSet().add(key, *members.toTypedArray())
            ops.expire(key, ttl)
            null
        })
    }

    fun putValue(key: String, value: String, ttl: Duration = RedisTtl.default) {
        redis.opsForValue().set(key, value, ttl)
    }
}