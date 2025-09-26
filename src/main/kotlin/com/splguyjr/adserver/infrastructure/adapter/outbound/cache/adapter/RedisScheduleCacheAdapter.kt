package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.adapter

import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.cache.ScheduleCachePort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisKeys
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisScheduleCacheAdapter(
    private val template: RedisTemplate<String, Schedule>,
    private val keys: RedisKeys
) : ScheduleCachePort {

    override fun put(scheduleId: Long, schedule: Schedule) {
        template.opsForValue().set(keys.schedule(scheduleId), schedule)
    }

    override fun get(scheduleId: Long): Schedule? =
        template.opsForValue().get(keys.schedule(scheduleId))

    override fun delete(scheduleId: Long) {
        template.delete(keys.schedule(scheduleId))
    }
}
