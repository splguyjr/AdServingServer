package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.readmodel.SpentBudget

data class Campaign(
    val id: Long,
    val totalBudget: Long,
    val totalSpentBudget: Long = 0L
) {
    init {
        require(totalBudget >= 0) { "총 예산은 음수일 수 없습니다. totalBudget=$totalBudget" }
        require(totalSpentBudget >= 0) { "누적 소진액은 음수일 수 없습니다. totalSpentBudget=$totalSpentBudget" }
        require(totalSpentBudget <= totalBudget) {
            "누적 소진액이 총 예산을 초과할 수 없습니다. spent=$totalSpentBudget, total=$totalBudget"
        }
    }

    /** 주어진 소진액(없으면 자기 값) 기준으로 총 예산 여유가 있는지 */
    fun hasTotalRoom(spent: SpentBudget?): Boolean {
        val used = spent?.totalSpentBudget ?: totalSpentBudget
        return used < totalBudget
    }
}