package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import java.time.Duration

object RedisKeys {
    /** 전체 서빙가능 AdSet 집합 */
    const val candidateAdSets = "ad:candidate:adsets"

    /** AdSet 별 후보 Creative 집합 */
    fun candidateCreativesOfAdSet(adSetId: Long) = "ad:candidate:adset:$adSetId:creatives"

    fun candidateSegmentOfAdSet(adSetId: Long) = "ad:candidate:adset:$adSetId:segment"
}

object RedisTtl {
    val default: Duration = Duration.ofMinutes(15)
}