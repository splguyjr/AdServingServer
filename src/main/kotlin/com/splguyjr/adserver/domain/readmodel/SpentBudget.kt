package com.splguyjr.adserver.domain.readmodel

/** Redis에 저장된 스케줄별 소진액(누적/일일) */
data class SpentBudget(
    val totalSpentBudget: Long,
    val dailySpentBudget: Long
)