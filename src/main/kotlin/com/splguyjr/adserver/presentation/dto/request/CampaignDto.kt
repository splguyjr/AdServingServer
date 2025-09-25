package com.splguyjr.adserver.presentation.dto.request

data class CampaignDto(
    val id: Long,
    val totalBudget: Long,
    val totalSpentBudget: Long          // 총 소진액
)