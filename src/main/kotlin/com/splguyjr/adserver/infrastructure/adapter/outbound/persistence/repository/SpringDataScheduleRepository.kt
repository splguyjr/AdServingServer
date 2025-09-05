package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.ScheduleEntity
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.projection.EligibleScheduleRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

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

    @Query(
        """
        select
            s.id as id,
            s.campaign.totalBudget as campaignTotalBudget,
            s.adSet.dailyBudget as adSetDailyBudget
        from ScheduleEntity s
        where s.adSet.status = com.splguyjr.adserver.domain.model.enum.Status.ON
          and s.creative.status = com.splguyjr.adserver.domain.model.enum.Status.ON
          and s.adSet.startDate <= :date and s.adSet.endDate >= :date
        """
    )
    fun findEligibleOnDateWithBudgets(date: LocalDate): List<EligibleScheduleRow>
}