package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.splguyjr.adserver.domain.model.Schedule
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class ScheduleRedisCache(
    private val template: RedisTemplate<String, Schedule>
) {
    fun put(scheduleId: Long, schedule: Schedule) {
        template.opsForValue().set(RedisKeys.schedule(scheduleId), schedule)
    }

    fun get(scheduleId: Long): Schedule? =
        template.opsForValue().get(RedisKeys.schedule(scheduleId))

    fun delete(scheduleId: Long) {
        template.delete(RedisKeys.schedule(scheduleId))
    }
}