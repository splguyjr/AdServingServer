package com.splguyjr.adserver.infrastructure.adapter.inbound.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisCacheWriter
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisKeys
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto.SegmentCache
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetCreativeRepository
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetRepository
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.SegmentRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 서빙 가능 후보군(AdSet, Creative, Segment)을 5분마다 MySQL에서 읽어
 * Redis에 캐시하는 스케줄러.
 *
 * 정책(AdSet 후보):
 *  - a.status = 'ON'
 *  - a.start_date <= 오늘 <= a.end_date
 *  - c.total_spent_budget < c.total_budget
 *  - a.daily_spent_budget < a.daily_budget
 *  - Creative.status = 'ON' (AdSet- Creative 관계에서 필터)
 */
@Component
class CandidateWarmupScheduler(
    private val adSetRepo: AdSetRepository,
    private val adSetCreativeRepo: AdSetCreativeRepository,
    private val segmentRepo: SegmentRepository,
    private val cache: RedisCacheWriter,
    private val om: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 */5 * * * *") // 매 5분
    @Transactional(readOnly = true)
    fun warmup() {
        val today = LocalDate.now()

        // 1) 서빙 가능 AdSet ID 조회
        val eligibleAdSetIds = adSetRepo.findEligibleAdSetIds(today)

        // 2) 후보 AdSet들에 연결된 상태 "ON"인 Creative 페어(adset_id, creative_id) 조회
        val pairs = if (eligibleAdSetIds.isEmpty()) emptyList()
        else adSetCreativeRepo.findOnCreativePairsByAdSetIds(eligibleAdSetIds)

        // 2-1) adSetId -> [creativeId, ...] 형태로 그룹핑
        val creativesByAdSet: Map<Long, List<Long>> =
            pairs.groupBy(
                keySelector = { (it[0] as Number).toLong() },
                valueTransform = { (it[1] as Number).toLong() }
            )

        // 3) 후보 AdSet의 세그먼트(단일)
        val segments = if (eligibleAdSetIds.isEmpty()) emptyList()
        else segmentRepo.findByAdSetIds(eligibleAdSetIds)

        // AdSet당 세그먼트는 1개
        val segmentByAdSet: Map<Long, SegmentCache> =
            segments.associate { s ->
                s.adSet.id to SegmentCache(
                    segmentType = s.segmentType,
                    minAge = s.minAge,
                    maxAge = s.maxAge,
                    gender = s.gender
                )
            }

        // 4) Redis 쓰기
        cache.overwriteSet(RedisKeys.candidateAdSets, eligibleAdSetIds.map { it.toString() })
        eligibleAdSetIds.forEach { adSetId ->
            val creativeIds = creativesByAdSet[adSetId].orEmpty().map { it.toString() }
            cache.overwriteSet(RedisKeys.candidateCreativesOfAdSet(adSetId), creativeIds)

            // 세그먼트 정보를 JSON으로 저장
            segmentByAdSet[adSetId]?.let { seg ->
                val segJson = om.writeValueAsString(seg)
                cache.putValue(RedisKeys.candidateSegmentOfAdSet(adSetId), segJson)
            }
        }

        log.info(
            "candidate warmup → adSets={}, pairs={}, segments={}",
            eligibleAdSetIds.size, pairs.size, segments.size
        )
    }
}