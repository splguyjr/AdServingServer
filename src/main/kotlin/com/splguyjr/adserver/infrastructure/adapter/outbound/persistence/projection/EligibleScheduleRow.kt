package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.projection

interface EligibleScheduleRow {
    fun getId(): Long
    fun getCampaignTotalBudget(): Long
    fun getAdSetDailyBudget(): Long
}