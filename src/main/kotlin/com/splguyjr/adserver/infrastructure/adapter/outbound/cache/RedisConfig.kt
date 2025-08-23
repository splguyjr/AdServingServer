package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto.CreativeCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer

@Configuration
class RedisConfig {

    /** cand:adsets 등 문자열 Set을 위한 템플릿 */
    @Bean
    fun stringRedisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate =
        StringRedisTemplate(connectionFactory)

    /** cand:adset:{id} 값(=CreativeCache 객체)을 위한 템플릿 */
    @Bean
    fun creativeRedisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, CreativeCache> {
        val template = RedisTemplate<String, CreativeCache>()
        template.setConnectionFactory(connectionFactory)

        val keySer = StringRedisSerializer()
        val valSer = Jackson2JsonRedisSerializer(objectMapper, CreativeCache::class.java)

        template.keySerializer = keySer
        template.hashKeySerializer = keySer
        template.valueSerializer = valSer
        template.hashValueSerializer = valSer

        template.afterPropertiesSet()
        return template
    }
}