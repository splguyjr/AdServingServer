package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.dto

import com.splguyjr.adserver.domain.model.enum.Status

data class AdSetCreativeCacheRow(
    val adSetId: Long,
    val imagePath: String,
    val logoPath: String,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val landingUrl: String,
    val status: Status
)