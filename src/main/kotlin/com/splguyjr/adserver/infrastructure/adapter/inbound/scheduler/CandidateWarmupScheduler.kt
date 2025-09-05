package com.splguyjr.adserver.infrastructure.adapter.inbound.scheduler

import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisCacheWriter
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.ScheduleRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 5분마다 "서빙 가능" 후보를 Redis에 전량 갱신.
 * - cand:adsets (Set): 값이 실제 존재하는 adSetId만 포함
 * - cand:adset:{adSetId} (Value): CreativeCache 객체
 */
@Component
class CandidateWarmupScheduler(
    private val scheduleRepo: ScheduleRepository,
    private val cache: RedisCacheWriter
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 */5 * * * *") // 매 5분
    @Transactional(readOnly = true)
    fun warmup() {
        val today = java.time.LocalDate.now()
        val eligibleAdSetScheduleIds = scheduleRepo.findEligibleAdSetScheduleIds(today)

        val rows = if (eligibleAdSetScheduleIds.isEmpty()) emptyList()
        else scheduleRepo.findCreativeCacheRowsByAdSetIds(eligibleAdSetScheduleIds, Status.ON)

        /*val cacheByAdSet: Map<Long, CreativeCache> = rows.associate { row ->
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

        val idsWithCreative: List<String> = cacheByAdSet.keys.map(Long::toString)
        cache.overwriteCandidateSet(RedisKeys.candidateAdSets, idsWithCreative)
        cacheByAdSet.forEach { (adSetId, creative) -> cache.putCreative(adSetId, creative) }

        val staleIds = eligibleAdSetScheduleIds.filterNot { cacheByAdSet.containsKey(it) }
        staleIds.forEach { adSetId -> cache.deleteCreative(adSetId) }

        log.info(
            "candidate warmup → eligibleAdSets={}, cachedAdSets={}, staleCleared={}",
            eligibleAdSetScheduleIds.size, cacheByAdSet.size, staleIds.size
        )*/
    }
}