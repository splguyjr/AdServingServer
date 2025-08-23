package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetJpa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface AdSetRepository : JpaRepository<AdSetJpa, Long> {
    @Query("select a.id from AdSetJpa a")
    fun findAllIds(): List<Long>

    @Query(
        value = """
        SELECT a.adset_id
        FROM ad_set a
        JOIN campaign c ON c.campaign_id = a.campaign_id
        WHERE a.status = 'ON'
          AND a.start_date <= :today
          AND a.end_date   >= :today
          AND c.total_spent_budget < c.total_budget
          AND a.daily_spent_budget < a.daily_budget
        """,
        nativeQuery = true
    )
    fun findEligibleAdSetIds(@Param("today") today: LocalDate): List<Long>
}