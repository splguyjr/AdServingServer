package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.model.enum.Gender
import com.splguyjr.adserver.domain.model.enum.SegmentType

data class Segment(
    val id: Long,
    val adSetId: Long,
    val segmentType: SegmentType,
    val minAge: Int?,
    val maxAge: Int?,
    val gender: Gender
)