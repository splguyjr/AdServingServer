package com.splguyjr.adserver.infrastructure.adapter.outbound.client.mapper

import com.splguyjr.adserver.domain.common.exception.enumOrThrow
import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto.ScheduleDto
import org.springframework.stereotype.Component

@Component
class ScheduleDtoMapper {

    fun toDomainList(dtos: List<ScheduleDto>): List<Schedule> =
        dtos.map { toDomain(it) }

    fun toDomain(dto: ScheduleDto): Schedule {
        val c = dto.campaign
        val a = dto.adSet
        val r = dto.creative

        val campaign = Campaign(
            id = c.id,
            totalBudget = c.totalBudget,
            totalSpentBudget = c.totalSpentBudget
        )

        val adSet = AdSet(
            id = a.id,
            campaignId = a.campaignId,
            // DTO가 문자열이면: LocalDate.parse(a.startDate)
            startDate = a.startDate,
            endDate   = a.endDate,
            dailyBudget = a.dailyBudget,
            bidAmount   = a.bidAmount,
            billingType = enumOrThrow<BillingType>(a.billingType, "adset.billingType"),
            status      = enumOrThrow<Status>(a.status, "adset.status"),
            dailySpentBudget = a.dailySpentBudget
        )

        val creative = Creative(
            id = r.id,
            imageUrl = r.imageUrl,
            logoUrl = r.logoUrl,
            title = r.title,
            subtitle = r.subtitle,
            description = r.description,
            landingUrl = r.landingUrl,
            status = enumOrThrow<Status>(r.status, "creative.status")
        )

        return Schedule(
            campaign = campaign,
            adSet = adSet,
            creative = creative
        )
    }
}