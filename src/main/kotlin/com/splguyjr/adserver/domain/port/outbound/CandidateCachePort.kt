package com.splguyjr.adserver.domain.port.outbound

/** 후보 스케줄 id 집합 캐시 관련 메소드 정의*/
interface CandidateCachePort {
    fun getCurrentCandidateScheduleIds(): Set<Long>
    /** 전체 초기화 후 새 후보군으로 교체 */
    fun overwriteCandidateScheduleIds(newIds: Set<Long>)
}