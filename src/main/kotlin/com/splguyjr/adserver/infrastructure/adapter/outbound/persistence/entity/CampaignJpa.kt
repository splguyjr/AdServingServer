package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "campaign")
class CampaignJpa(
    @Id
    @Column(name = "campaign_id")
    val id: Long,

    @Column(name = "total_budget", nullable = false)
    var totalBudget: Long,

    @Column(name = "total_spent_budget", nullable = false)
    var totalSpentBudget: Long
)