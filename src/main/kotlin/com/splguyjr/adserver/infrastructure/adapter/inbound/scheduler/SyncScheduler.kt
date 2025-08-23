package com.splguyjr.adserver.infrastructure.adapter.inbound.scheduler

import com.splguyjr.adserver.domain.port.outbound.AdPlatformClientPort
import com.splguyjr.adserver.domain.port.outbound.AdRepositoryPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetRepository
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.CampaignRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SyncScheduler(
    private val client: AdPlatformClientPort,      // 외부 플랫폼 호출 (DTO→도메인 변환까지 포함)
    private val repo: AdRepositoryPort,            // 도메인 → JPA 저장
    private val campaignRepo: CampaignRepository, // ID 목록 조회용 (인프라↔인프라)
    private val adSetRepo: AdSetRepository
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

        // 2) 세트 (DB에 존재하는 캠페인 ID 순회)
        val campaignIds = campaignRepo.findAllIds()
        var adSetCount = 0
        campaignIds.forEach { cid ->
            val adSets = client.fetchAdSets(cid)
            adSets.forEach { repo.upsertAdSet(it) }
            adSetCount += adSets.size
        }
        log.info("synced adSets: {}", adSetCount)

        // 3) 소재 + 관계 (DB에 존재하는 애드셋 ID 순회)
        val adSetIds = adSetRepo.findAllIds()
        var creativeCount = 0
        var segmentCount = 0

        adSetIds.forEach { aid ->
            val creatives = client.fetchCreatives(aid)
            creatives.forEach {
                repo.upsertCreative(it)
                it.defaultAdSetId?.let { defaultAdSetId ->
                    repo.upsertAdSetCreative(defaultAdSetId, it.id)
                }
            }
            creativeCount += creatives.size

            // 4) 세그먼트 (플랫폼이 변경분만 주는 가정)
            val segments = client.fetchSegments(aid)
            segments.forEach { repo.upsertSegment(it) }
            segmentCount += segments.size
        }

        log.info("synced creatives: {}, segments: {}", creativeCount, segmentCount)
    }
}