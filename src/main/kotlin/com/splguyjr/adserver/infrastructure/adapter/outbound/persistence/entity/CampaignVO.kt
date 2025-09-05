package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class CampaignVO(

    @Column(table = "campaign", name = "campaign_id", nullable = false)
    var campaignId: Long? = null,

    @Column(table = "campaign", name = "total_budget", nullable = false)
    var totalBudget: Long? = null
)


