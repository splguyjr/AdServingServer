package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto

import com.splguyjr.adserver.domain.model.enum.Gender
import com.splguyjr.adserver.domain.model.enum.SegmentType

data class SegmentCache(
    val segmentType: SegmentType,
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val gender: Gender
)