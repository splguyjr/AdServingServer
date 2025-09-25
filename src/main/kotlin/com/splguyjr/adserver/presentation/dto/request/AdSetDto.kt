package com.splguyjr.adserver.presentation.dto.request

import java.time.LocalDate

data class AdSetDto(
    val id: Long,
    val campaignId: Long,
    val startDate: LocalDate,           // 문자열이면 String으로 받고 LocalDate.parse(...) 하세요
    val endDate: LocalDate,
    val dailyBudget: Long,
    val bidAmount: Long,
    val billingType: String,            // CPC/CPM/CPA
    val status: String,                 // ON/OFF
    val dailySpentBudget: Long          // 일 소진액
)