package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.adapter

import com.splguyjr.adserver.domain.port.outbound.cache.TotalSpentPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.invalidation.CacheEventPublisher
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.l1.cache.CacheNames
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/** 합성(@Primary): 읽기 L1→미스 시 L2, INCR/RESET 후 L1 evict + Pub */
@Primary
@Component
class CachingTotalSpentAdapter(
    private val cacheManager: CacheManager,
    @Qualifier("redisTotalSpent") private val delegate: TotalSpentPort, // L1 실패시 L2에 직접 접근하는 로직 수행하기 위한 의존성
    private val publisher: CacheEventPublisher
) : TotalSpentPort {

    private val cache get() = cacheManager.getCache(CacheNames.TOTAL)!!

    // L1 히트면 빠르게 반환, 미스면 L2에서 읽어 L1 채움
    override fun getTotal(scheduleId: Long): Long? =
        cache.get(scheduleId, java.lang.Long::class.java)?.toLong()
            ?: delegate.getTotal(scheduleId)?.also { cache.put(scheduleId, it) }

    // 광고 서빙에 따른 과금, L2에 원자 증가, L1과 모든 노드의 L1을 무효화.
    override fun incrTotal(scheduleId: Long, delta: Long): Long {
        val v = delegate.incrTotal(scheduleId, delta) // L2 원자 증가
        cache.evict(scheduleId)                       // 내 L1 무효화
        publisher.publishTotalEvict(scheduleId)       // 분산 무효화
        return v
    }

    override fun initTotalIfAbsent(scheduleId: Long) {
        delegate.initTotalIfAbsent(scheduleId)
    }
}