package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetCreativeJpa
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.id.AdSetCreativeId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AdSetCreativeRepository : JpaRepository<AdSetCreativeJpa, AdSetCreativeId> {
    @Query(
        value = """
        SELECT ac.adset_id AS adSetId, ac.creative_id AS creativeId
        FROM adset_creative ac
        JOIN creative c ON c.creative_id = ac.creative_id
        WHERE ac.adset_id IN (:adSetIds)
          AND c.status = 'ON'
        """,
        nativeQuery = true
    )
    fun findOnCreativePairsByAdSetIds(@Param("adSetIds") adSetIds: List<Long>): List<Array<Any>>
}