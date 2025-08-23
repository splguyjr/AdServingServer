package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper

import com.splguyjr.adserver.domain.model.Segment
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetJpa
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.SegmentJpa

object SegmentMapper {
    fun toDomain(jpa: SegmentJpa) = Segment(
        id = jpa.id,
        adSetId = jpa.adSet.id,
        segmentType = jpa.segmentType,
        minAge = jpa.minAge,
        maxAge = jpa.maxAge,
        gender = jpa.gender
    )

    fun toEntity(domain: Segment, adSet: AdSetJpa) = SegmentJpa(
        id = domain.id,
        adSet = adSet,
        segmentType = domain.segmentType,
        minAge = domain.minAge,
        maxAge = domain.maxAge,
        gender = domain.gender
    )
}