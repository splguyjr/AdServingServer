package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.ScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ScheduleRepository : JpaRepository<ScheduleEntity, Long> {

    @Query(
        value = """
        SELECT s.schedule_id
        FROM schedule s
        JOIN ad_set a ON a.schedule_id = s.schedule_id
        WHERE a.status = 'ON'
          AND a.start_date <= :today
          AND a.end_date   >= :today
          AND a.daily_budget > 0
        """,
        nativeQuery = true
    )
    fun findEligibleAdSetScheduleIds(@Param("today") today: LocalDate): List<Long>

    @Query(
        value = """
        SELECT a.adset_id as adSetId,
               c.image_path as imagePath,
               c.logo_path as logoPath,
               c.title as title,
               c.subtitle as subtitle,
               c.description as description,
               c.landing_url as landingUrl,
               c.status as status
        FROM schedule s
        JOIN ad_set a ON a.schedule_id = s.schedule_id
        JOIN creative c ON c.schedule_id = s.schedule_id
        WHERE a.adset_id IN (:adSetIds)
          AND c.status = :status
        """,
        nativeQuery = true
    )
    fun findCreativeCacheRowsByAdSetIds(
        @Param("adSetIds") adSetIds: List<Long>,
        @Param("status") status: Status
    ): List<CreativeCacheRow>
}

interface CreativeCacheRow {
    fun getAdSetId(): Long
    fun getImagePath(): String
    fun getLogoPath(): String
    fun getTitle(): String
    fun getSubtitle(): String?
    fun getDescription(): String?
    fun getLandingUrl(): String
    fun getStatus(): Status
}


