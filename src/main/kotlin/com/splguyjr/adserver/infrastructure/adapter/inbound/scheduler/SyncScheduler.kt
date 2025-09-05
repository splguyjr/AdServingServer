package com.splguyjr.adserver.infrastructure.adapter.inbound.scheduler

import com.splguyjr.adserver.domain.port.outbound.AdPlatformClientPort
import com.splguyjr.adserver.domain.port.outbound.AdRepositoryPort
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SyncScheduler(
    private val client: AdPlatformClientPort,      // 외부 플랫폼 호출 (DTO→도메인 변환까지 포함)
    private val repo: AdRepositoryPort            // 도메인 → 저장 (현재 no-op)
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 플랫폼이 알아서 "신규/변경분만" 내려준다는 전제로 그냥 호출해서 그대로 upsert만 한다.
     * 20초 주기 자동 호출
     */
    @Scheduled(fixedDelay = 20000)
    @Transactional
    fun syncAll() {
        // 1) 캠페인
        val campaigns = client.fetchCampaigns()
        campaigns.forEach { repo.upsertCampaign(it) }
        log.info("synced campaigns: {}", campaigns.size)

        // 2) 세트 (플랫폼에서 캠페인별로 조회 후 저장)
        var adSetCount = 0
        campaigns.forEach { c ->
            val adSets = client.fetchAdSets(c.id)
            adSets.forEach { repo.upsertAdSet(it) }
            adSetCount += adSets.size
        }
        log.info("synced adSets: {}", adSetCount)

        // 3) 소재는 현재 no-op 저장
        var creativeCount = 0
        campaigns.forEach { c ->
            val adSets = client.fetchAdSets(c.id)
            adSets.forEach { a ->
                val creatives = client.fetchCreatives(a.id)
                creatives.forEach { repo.upsertCreative(it) }
                creativeCount += creatives.size
            }
        }
        log.info("synced creatives: {}", creativeCount)
    }
}