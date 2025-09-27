package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.invalidation

import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.cache.CacheNames
import jakarta.annotation.PostConstruct
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

/** Pub/Sub 구독(SUB) → 로컬 캐시(evict) */
@Configuration
class CacheEventSubscriber(
    private val container: RedisMessageListenerContainer,
    private val cacheManager: CacheManager,
    private val topics: RedisTopics
) {
    @PostConstruct
    fun subscribe() {
        container.addMessageListener(listener(CacheNames.TOTAL), ChannelTopic(topics.evictTotal()))
        container.addMessageListener(listener(CacheNames.DAILY), ChannelTopic(topics.evictDaily()))
    }
    private fun listener(cacheName: String) = MessageListener { message: Message, _ ->
        val id = String(message.body).toLongOrNull() ?: return@MessageListener
        cacheManager.getCache(cacheName)?.evict(id)
    }
}