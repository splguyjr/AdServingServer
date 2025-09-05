package com.splguyjr.adserver.domain.port.outbound

import com.splguyjr.adserver.domain.model.Schedule

/**
 * 도메인 관점의 저장소 포트 (Outbound Port).
 * - 인프라 세부사항(JPA Entity 등)에 의존하지 않도록 도메인 타입만 사용
 */
interface ScheduleRepository {
    /** 자연키(campaignId, adSetId, creativeId)에 해당하는 스케줄 DB PK 반환 (없으면 null) */
    fun findIdByNaturalKey(campaignId: Long, adSetId: Long, creativeId: Long): Long?

    /** 없으면 insert, 있으면 update 후 PK 반환 */
    fun saveOrUpdate(schedule: Schedule): Long

    /** 일괄 upsert 후 개수 반환*/
    fun saveOrUpdateAll(schedules: List<Schedule>): Int =
        schedules.onEach { saveOrUpdate(it) }.size
}