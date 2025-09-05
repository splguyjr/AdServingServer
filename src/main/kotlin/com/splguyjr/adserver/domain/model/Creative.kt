package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.model.enum.Status

data class Creative(
    val id: Long,
    val imagePath: String,
    val logoPath: String,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val landingUrl: String,
    val status: Status
)