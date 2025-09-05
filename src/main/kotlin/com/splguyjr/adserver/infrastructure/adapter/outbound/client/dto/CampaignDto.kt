package com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto

data class CampaignDto(
    val id: Long,
    val totalBudget: Long,
    val totalSpentBudget: Long          // 총 소진액
)