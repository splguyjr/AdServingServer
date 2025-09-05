package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.model.enum.Status

data class Creative(
    val id: Long,
    val imageUrl: String,
    val logoUrl: String,
    val title: String,
    val subtitle: String?,
    val landingUrl: String,
    val status: Status
)