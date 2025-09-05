package com.splguyjr.adserver.domain.readmodel

/** 상태/기간 조건을 만족하는 스케줄의 식별자와 예산 상한치(총/일일) */
data class EligibleScheduleBudget(
    val scheduleId: Long,
    val campaignTotalBudget: Long,
    val adSetDailyBudget: Long
)