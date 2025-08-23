package com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto

data class CreativeDto(
    val id: Long,
    val defaultAdSetId: Long?,
    val imagePath: String,
    val logoPath: String,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val landingUrl: String,
    val status: String
)