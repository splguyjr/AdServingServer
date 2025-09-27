package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.adapter

import com.splguyjr.adserver.domain.port.outbound.cache.DailySpentPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.invalidation.CacheEventPublisher
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.cache.CacheNames
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/** 합성(@Primary): 읽기 L1→미스 시 L2, INCR/RESET 후 L1 evict + Pub */
@Primary
@Component
class CachingDailySpentAdapter(
    private val cacheManager: CacheManager,
    @Qualifier("redisDailySpent") private val delegate: DailySpentPort, // L1 실패시 L2에 직접 접근하는 로직 수행
    private val publisher: CacheEventPublisher
) : DailySpentPort {

    private val cache get() = cacheManager.getCache(CacheNames.DAILY)!!

    // L1 히트면 빠르게 반환, 미스면 L2에서 읽어 L1 채움
    override fun getDaily(scheduleId: Long): Long? =
        cache.get(scheduleId, java.lang.Long::class.java)?.toLong()
            ?: delegate.getDaily(scheduleId)?.also { cache.put(scheduleId, it) }

    // 광고 서빙에 따른 과금, L2에 원자 증가, L1과 모든 노드의 L1을 무효화.
    override fun incrDaily(scheduleId: Long, delta: Long): Long {
        val v = delegate.incrDaily(scheduleId, delta)
        cache.evict(scheduleId)
        publisher.publishDailyEvict(scheduleId)
        return v
    }

    // 자정 리셋(일일 소진액 0). L2에서 원자적으로 0 교체, L1과 모든 노드 L1 무효화
    override fun resetDailyToZero(scheduleId: Long): Long {
        val prev = delegate.resetDailyToZero(scheduleId)
        cache.evict(scheduleId)
        publisher.publishDailyEvict(scheduleId)
        return prev
    }

    override fun initDailyIfAbsent(scheduleId: Long) {
        delegate.initDailyIfAbsent(scheduleId)
    }
}