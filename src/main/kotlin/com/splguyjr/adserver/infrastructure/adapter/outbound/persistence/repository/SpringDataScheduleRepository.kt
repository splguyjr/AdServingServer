package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.ScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Spring Data JPA 전용 리포지토리.
 * 도메인 포트와는 분리해서 프레임워크 의존을 인프라에 가둔다.
 */
interface SpringDataScheduleRepository : JpaRepository<ScheduleEntity, Long> {

    @Query(
        """
        select s from ScheduleEntity s
         where s.campaign.campaignId = :campaignId
           and s.adSet.adSetId = :adSetId
           and s.creative.creativeId = :creativeId
        """
    )
    fun findOneByNaturalKey(
        campaignId: Long,
        adSetId: Long,
        creativeId: Long
    ): ScheduleEntity?
}