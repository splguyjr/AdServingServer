package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

// Redis 키 네이밍 전용 유틸리티 클래스
object RedisKeys {
    /** 서빙 가능한 AdSet ID 전체 풀(Set) */
    const val candidateAdSets = "cand:adsets"

    /** 특정 AdSet의 '단일' Creative ID를 담는 String 키 */
    fun adsetCreative(adSetId: Long) = "cand:adset:$adSetId"
}