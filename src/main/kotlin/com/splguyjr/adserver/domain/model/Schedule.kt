package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.readmodel.SpentBudget
import java.time.LocalDate

data class Schedule(
    val campaign: Campaign,
    val adSet: AdSet,
    val creative: Creative
) {
    /**
     * 도메인 기준 “서빙 가능” 판정:
     *  - 세트/소재 활성
     *  - 오늘 날짜가 세트 기간 범위 내
     *  - 예산 여유(총/일)
     */
    fun isEligibleToday(today: LocalDate, spent: SpentBudget?): Boolean =
        adSet.isActiveOn(today) &&
                creative.isActive() &&
                campaign.hasTotalRoom(spent, adSet.bidAmount) &&
                adSet.hasDailyRoom(spent, adSet.bidAmount)
}