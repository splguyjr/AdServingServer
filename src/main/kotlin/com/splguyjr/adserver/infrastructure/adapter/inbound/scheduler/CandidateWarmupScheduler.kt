package com.splguyjr.adserver.infrastructure.adapter.inbound.scheduler

import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisCacheWriter
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisKeys
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto.CreativeCache
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetCreativeRepository
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 5분마다 "서빙 가능" 후보를 Redis에 전량 갱신.
 * - cand:adsets (Set): 값이 실제 존재하는 adSetId만 포함
 * - cand:adset:{adSetId} (Value): CreativeCache 객체
 */
@Component
class CandidateWarmupScheduler(
    private val adSetRepo: AdSetRepository,
    private val adSetCreativeRepo: AdSetCreativeRepository,
    private val cache: RedisCacheWriter
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 */5 * * * *") // 매 5분
    @Transactional(readOnly = true)
    fun warmup() {
        val today = LocalDate.now()

        // 1) "서빙 가능" AdSet ID (상태/기간/예산 조건은 DB에서 필터링)
        val eligibleAdSetIds = adSetRepo.findEligibleAdSetIds(today)

        // 2) 각 AdSet의 'ON' Creative 표시용 필드 조회
        val rows = if (eligibleAdSetIds.isEmpty()) emptyList()
        else adSetCreativeRepo.findCreativeCacheRowsByAdSetIds(eligibleAdSetIds, Status.ON)

        // 3) adSetId -> CreativeCache 매핑 (Enum → 문자열)
        val cacheByAdSet: Map<Long, CreativeCache> = rows.associate { row ->
            row.adSetId to CreativeCache(
                imagePath = row.imagePath,
                logoPath = row.logoPath,
                title = row.title,
                subtitle = row.subtitle,
                description = row.description,
                landingUrl = row.landingUrl,
                status = row.status.name
            )
        }

        // cand:adsets에는 "ON 상태의 연결된 소재가 존재하는" adSetId만 넣는다
        val idsWithCreative: List<String> = cacheByAdSet.keys.map(Long::toString)

        // 4-1) 후보 AdSet 풀(Set) 덮어쓰기 — 값 있는 세트만
        cache.overwriteCandidateSet(RedisKeys.candidateAdSets, idsWithCreative)

        // 4-2) 각 AdSet의 CreativeCache 객체 저장
        cacheByAdSet.forEach { (adSetId, creative) ->
            cache.putCreative(adSetId, creative)
        }

        // 4-3) 이번 웜업에서 값이 사라진 세트 키 정리(깨끗하게 유지)
        val staleIds = eligibleAdSetIds.filterNot { cacheByAdSet.containsKey(it) }
        staleIds.forEach { adSetId -> cache.deleteCreative(adSetId) }

        log.info(
            "candidate warmup → eligibleAdSets={}, cachedAdSets={}, staleCleared={}",
            eligibleAdSetIds.size, cacheByAdSet.size, staleIds.size
        )
    }
}