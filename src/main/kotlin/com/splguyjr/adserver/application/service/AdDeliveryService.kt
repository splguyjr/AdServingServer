package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.dto.AdDeliveryResponse
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisCacheWriter
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.RedisKeys
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto.CreativeCache
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetCreativeRepository
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.AdSetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.random.Random

@Service
class AdDeliveryService(
    private val cache: RedisCacheWriter,
    private val adSetRepo: AdSetRepository,
    private val adSetCreativeRepo: AdSetCreativeRepository
) {
    /** 1차 Redis, 미스 시 MySQL로 채워넣고 한 개 전달 */
    @Transactional(readOnly = true)
    fun deliverOne(): AdDeliveryResponse? {
        // 1) Redis 시도
        tryPickFromCache()?.let { return it }

        // 2) Fallback: DB 재조회 → Redis 갱신(간이) → 하나 선택
        val today = LocalDate.now()
        val eligibleAdSetIds = adSetRepo.findEligibleAdSetIds(today)
        if (eligibleAdSetIds.isEmpty()) return null

        val rows = adSetCreativeRepo.findCreativeCacheRowsByAdSetIds(eligibleAdSetIds, Status.ON)
        if (rows.isEmpty()) return null

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

        val idsWithCreative = cacheByAdSet.keys.map(Long::toString)
        if (idsWithCreative.isEmpty()) return null

        // cand:adsets에는 값이 있는 세트만
        cache.overwriteCandidateSet(RedisKeys.candidateAdSets, idsWithCreative)
        // 세트별 CreativeCache 저장
        cacheByAdSet.forEach { (adSetId, creative) -> cache.putCreative(adSetId, creative) }

        // 하나 골라 반환
        val chosenAdSetId = idsWithCreative[Random.nextInt(idsWithCreative.size)].toLong()
        return AdDeliveryResponse(chosenAdSetId, cacheByAdSet[chosenAdSetId]!!)
    }

    private fun tryPickFromCache(): AdDeliveryResponse? {
        val adSetIdStr = cache.srandAdSet() ?: return null
        val adSetId = adSetIdStr.toLongOrNull() ?: return null
        val creative = cache.getCreative(adSetId) ?: return null
        return AdDeliveryResponse(adSetId, creative)
    }
}