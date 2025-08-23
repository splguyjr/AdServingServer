package com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto

data class SegmentDto(
    val id: Long,
    val adSetId: Long,
    val segmentType: String,
    val minAge: Int,
    val maxAge: Int,
    val gender: String
)