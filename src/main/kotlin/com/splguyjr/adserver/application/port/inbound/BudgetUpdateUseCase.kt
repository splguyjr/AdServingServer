package com.splguyjr.adserver.application.port.inbound

interface BudgetUpdateUseCase {
    /** KST 자정 배치: 모든 후보 스케줄에 대해 daily→total 반영 후 daily=0 */
    fun rollDailyIntoTotalForAll(): Int
}