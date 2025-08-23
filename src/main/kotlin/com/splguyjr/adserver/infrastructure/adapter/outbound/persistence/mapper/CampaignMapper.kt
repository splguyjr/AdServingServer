package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper

import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CampaignJpa

object CampaignMapper {
    fun toDomain(jpa: CampaignJpa) = Campaign(
        jpa.id,
        jpa.totalBudget,
        jpa.totalSpentBudget
    )

    fun toEntity(domain: Campaign) = CampaignJpa(
        domain.id,
        domain.totalBudget,
        domain.totalSpentBudget)

    // update용, totalSpentBudget은 업데이트 X
    fun applyUpdates(target: CampaignJpa, from: Campaign) {
        target.totalBudget = from.totalBudget
    }
}