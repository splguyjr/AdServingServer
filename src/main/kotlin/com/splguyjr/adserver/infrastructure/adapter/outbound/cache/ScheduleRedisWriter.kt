package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.splguyjr.adserver.domain.model.Schedule
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class ScheduleRedisWriter(
    private val scheduleRedisTemplate: RedisTemplate<String, Schedule>
) {
    fun put(scheduleId: Long, schedule: Schedule) {
        scheduleRedisTemplate.opsForValue().set(RedisKeys.schedule(scheduleId), schedule)
    }
    fun get(scheduleId: Long): Schedule? =
        scheduleRedisTemplate.opsForValue().get(RedisKeys.schedule(scheduleId))
    fun delete(scheduleId: Long) {
        scheduleRedisTemplate.delete(RedisKeys.schedule(scheduleId))
    }
}