package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import java.time.LocalDate

data class AdSet(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyBudget: Long,
    val billingType: BillingType,
    val status: Status,
    val dailySpentBudget: Long = 0L
)