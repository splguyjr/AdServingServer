package com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto

data class CreativeCache(
    val imagePath: String,
    val logoPath: String,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val landingUrl: String,
    val status: String,
)