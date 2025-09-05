package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
data class AdSetVO(

    @Column(table = "ad_set", name = "campaign_id", nullable = false)
    var campaignId: Long? = null,

    @Column(table = "ad_set", name = "start_date", nullable = false)
    var startDate: LocalDate? = null,

    @Column(table = "ad_set", name = "end_date", nullable = false)
    var endDate: LocalDate? = null,

    @Column(table = "ad_set", name = "daily_budget", nullable = false)
    var dailyBudget: Long? = null,

    @Column(table = "ad_set", name = "billing_type", nullable = false, length = 10)
    var billingType: BillingType? = null,

    @Column(table = "ad_set", name = "status", nullable = false, length = 5)
    var status: Status? = null
)


