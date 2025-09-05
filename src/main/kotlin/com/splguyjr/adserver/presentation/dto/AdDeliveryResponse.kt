package com.splguyjr.adserver.presentation.dto

import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.readmodel.SpentBudget

data class AdDeliveryResponse(
    val scheduleId: Long,
    val imageUrl: String?,
    val logoUrl: String?,
    val title: String?,
    val subtitle: String?,
    val landingUrl: String,
    val totalSpentBudget: Long?,
    val dailySpentBudget: Long?
) {
    companion object {
        /** 소진액 포함 변환 */
        fun from(scheduleId: Long, schedule: Schedule, spent: SpentBudget?): AdDeliveryResponse =
            AdDeliveryResponse(
                scheduleId = scheduleId,
                imageUrl = schedule.creative.imageUrl,
                logoUrl = schedule.creative.logoUrl,
                title = schedule.creative.title,
                subtitle = schedule.creative.subtitle,
                landingUrl = schedule.creative.landingUrl,
                totalSpentBudget = spent?.totalSpentBudget,
                dailySpentBudget = spent?.dailySpentBudget
            )
    }
}