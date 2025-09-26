package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.splguyjr.adserver.domain.model.Schedule
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
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, Schedule> {
        val template = RedisTemplate<String, Schedule>()
        template.setConnectionFactory(connectionFactory)

        val keySerializer = StringRedisSerializer()
        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, Schedule::class.java)

        template.keySerializer = keySerializer
        template.hashKeySerializer = keySerializer
        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer

        template.afterPropertiesSet()
        return template
    }
}