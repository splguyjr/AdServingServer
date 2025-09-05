package com.splguyjr.adserver.domain.port.outbound

import com.splguyjr.adserver.domain.readmodel.SpentBudget

/** Redis 스케줄 관련 총 소진액, 일일 소진액 정보 조회 */
interface SpentBudgetReaderPort {
    fun get(scheduleId: Long): SpentBudget?
}