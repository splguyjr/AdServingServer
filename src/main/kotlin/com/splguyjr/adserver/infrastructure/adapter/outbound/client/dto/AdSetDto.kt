package com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto

import java.time.LocalDate

data class AdSetDto(
    val id: Long,
    val campaignId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyBudget: Long,
    val bidAmount: Long,
    val billingType: String,
    val status: String
)