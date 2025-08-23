package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "ad_set")
class AdSetJpa(
    @Id
    @Column(name = "adset_id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    val campaign: CampaignJpa,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate,

    @Column(name = "daily_budget", nullable = false)
    var dailyBudget: Long,

    @Column(name = "bid_amount", nullable = false)
    var bidAmount: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, length = 10)
    var billingType: BillingType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 5)
    var status: Status,

    @Column(name = "daily_spent_budget", nullable = false)
    var dailySpentBudget: Long
)