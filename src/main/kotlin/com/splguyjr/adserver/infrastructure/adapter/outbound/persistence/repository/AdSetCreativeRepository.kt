package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.dto.AdSetCreativeCacheRow
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetCreativeJpa
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.id.AdSetCreativeId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AdSetCreativeRepository : JpaRepository<AdSetCreativeJpa, AdSetCreativeId> {

    // 중간 테이블 기준으로 세트 - 소재 조인해서 필요한 정보 projection 반환
    @Query(
        """
        select new com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.dto.AdSetCreativeCacheRow(
            ac.adSet.id,
            c.imagePath,
            c.logoPath,
            c.title,
            c.subtitle,
            c.description,
            c.landingUrl,
            c.status
        )
        from AdSetCreativeJpa ac
        join ac.creative c
        where ac.adSet.id in :adSetIds
          and c.status = :onStatus
        """
    )
    fun findCreativeCacheRowsByAdSetIds(
        @Param("adSetIds") adSetIds: List<Long>,
        @Param("onStatus") onStatus: Status
    ): List<AdSetCreativeCacheRow>
}