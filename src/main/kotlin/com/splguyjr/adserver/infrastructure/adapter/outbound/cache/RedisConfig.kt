package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun stringRedisTemplate(cf: RedisConnectionFactory): StringRedisTemplate =
        StringRedisTemplate(cf)

    /** scheduleId:Schedule 값을 위한 템플릿 */
    @Bean
    fun scheduleRedisTemplate(
        cf: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, Schedule> {
        val t = RedisTemplate<String, Schedule>()
        t.setConnectionFactory(cf)
        val key = StringRedisSerializer()
        val value = Jackson2JsonRedisSerializer(objectMapper, Schedule::class.java)
        t.keySerializer = key
        t.hashKeySerializer = key
        t.valueSerializer = value
        t.hashValueSerializer = value
        t.afterPropertiesSet()
        return t
    }

    /** scheduleId:SpentBudget 값을 위한 템플릿 */
    @Bean
    fun spentBudgetRedisTemplate(
        cf: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, SpentBudget> {
        val t = RedisTemplate<String, SpentBudget>()
        t.setConnectionFactory(cf)
        val key = StringRedisSerializer()
        val value = Jackson2JsonRedisSerializer(objectMapper, SpentBudget::class.java)
        t.keySerializer = key
        t.hashKeySerializer = key
        t.valueSerializer = value
        t.hashValueSerializer = value
        t.afterPropertiesSet()
        return t
    }
}