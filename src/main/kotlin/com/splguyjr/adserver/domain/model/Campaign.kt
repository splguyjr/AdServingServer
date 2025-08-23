package com.splguyjr.adserver.domain.model

data class Campaign(
    val id: Long,
    val totalBudget: Long,
    val totalSpentBudget: Long = 0L
)