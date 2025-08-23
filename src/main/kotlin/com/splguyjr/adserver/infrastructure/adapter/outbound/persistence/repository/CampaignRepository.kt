package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CampaignJpa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CampaignRepository : JpaRepository<CampaignJpa, Long> {
    @Query("select c.id from CampaignJpa c")
    fun findAllIds(): List<Long>

    @Query(
        value = """
        SELECT c.campaign_id
        FROM campaign c
        WHERE c.total_spent_budget < c.total_budget
        """,
        nativeQuery = true
    )
    fun findIdsUnderTotalBudget(): List<Long>
}