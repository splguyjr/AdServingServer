package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import java.time.LocalDate

data class AdSet(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyBudget: Long,
    val billingType: BillingType,
    val status: Status,
    val dailySpentBudget: Long = 0L,
    val bidAmount: Long = 0L
) {
    init {
        require(!startDate.isAfter(endDate)) {
            "시작일은 종료일보다 늦을 수 없습니다. start=$startDate, end=$endDate"
        }
        require(dailyBudget >= 0) { "일 예산은 음수일 수 없습니다. dailyBudget=$dailyBudget" }
        require(dailySpentBudget >= 0) { "일 소진액은 음수일 수 없습니다. dailySpentBudget=$dailySpentBudget" }
        require(dailySpentBudget <= dailyBudget) {
            "일 소진액이 일 예산을 초과할 수 없습니다. spentDaily=$dailySpentBudget, daily=$dailyBudget"
        }
    }

    /** 해당 날짜에 세트가 활성 상태인지 */
    fun isActiveOn(date: LocalDate): Boolean =
        status == Status.ON &&
                (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isEqual(endDate) || date.isBefore(endDate))

    /** 주어진 소진액(없으면 자기 값) 기준으로 일일 예산 여유가 있는지 */
    fun hasDailyRoom(spent: SpentBudget?): Boolean {
        val used = spent?.dailySpentBudget ?: dailySpentBudget
        return used < dailyBudget
    }
}
