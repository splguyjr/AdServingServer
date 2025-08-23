package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetJpa
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CampaignJpa

object AdSetMapper {
    fun toDomain(jpa: AdSetJpa) = AdSet(
        id = jpa.id,
        campaignId = jpa.campaign.id,
        startDate = jpa.startDate,
        endDate = jpa.endDate,
        dailyBudget = jpa.dailyBudget,
        bidAmount = jpa.bidAmount,
        billingType = jpa.billingType,
        status     = jpa.status,
        dailySpentBudget = jpa.dailySpentBudget
    )

    fun toEntity(domain: AdSet, campaign: CampaignJpa) = AdSetJpa(
        id = domain.id,
        campaign = campaign,
        startDate = domain.startDate,
        endDate = domain.endDate,
        dailyBudget = domain.dailyBudget,
        bidAmount = domain.bidAmount,
        billingType = domain.billingType,
        status = domain.status,
        dailySpentBudget = domain.dailySpentBudget
    )

    // update용
    fun applyUpdates(target: AdSetJpa, from: AdSet, campaign: CampaignJpa) {
        target.startDate   = from.startDate
        target.endDate     = from.endDate
        target.dailyBudget = from.dailyBudget
        target.bidAmount   = from.bidAmount
        target.billingType = from.billingType
        target.status      = from.status
    }
}