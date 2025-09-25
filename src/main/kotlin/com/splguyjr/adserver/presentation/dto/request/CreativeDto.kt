package com.splguyjr.adserver.presentation.dto.request

data class CreativeDto(
    val id: Long,
    val imageUrl: String,
    val logoUrl: String,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val landingUrl: String,
    val status: String                  // ON/OFF
)