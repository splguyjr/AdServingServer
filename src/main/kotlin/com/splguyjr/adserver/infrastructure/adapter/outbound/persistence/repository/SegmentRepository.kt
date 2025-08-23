package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.SegmentJpa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SegmentRepository : JpaRepository<SegmentJpa, Long> {

    @Query("select s from SegmentJpa s where s.adSet.id in :adSetIds")
    fun findByAdSetIds(@Param("adSetIds") adSetIds: List<Long>): List<SegmentJpa>
}