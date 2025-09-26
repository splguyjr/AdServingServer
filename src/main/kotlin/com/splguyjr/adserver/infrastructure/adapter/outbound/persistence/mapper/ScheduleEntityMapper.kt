package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetVO
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CampaignVO
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CreativeVO
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.ScheduleEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ScheduleEntityMapper {

    /** 도메인 → 신규 엔티티 */
    fun toEntity(s: Schedule): ScheduleEntity =
        ScheduleEntity(
            campaign = CampaignVO(
                campaignId = s.campaign.id,
                totalBudget = s.campaign.totalBudget
            ),
            adSet = AdSetVO(
                adSetId     = s.adSet.id,
                startDate   = s.adSet.startDate,
                endDate     = s.adSet.endDate,
                dailyBudget = s.adSet.dailyBudget,
                billingType = s.adSet.billingType,
                status      = s.adSet.status
            ),
            creative = CreativeVO(
                creativeId = s.creative.id,
                imageUrl   = s.creative.imageUrl,
                logoUrl    = s.creative.logoUrl,
                title      = s.creative.title,
                subtitle   = s.creative.subtitle,
                landingUrl = s.creative.landingUrl,
                status     = s.creative.status
            ),
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

    /** 기존 엔티티에 도메인 적용 (update) */
    fun applyDomain(target: ScheduleEntity, s: Schedule) {
        target.campaign.totalBudget = s.campaign.totalBudget

        target.adSet.apply {
            adSetId     = s.adSet.id
            startDate   = s.adSet.startDate
            endDate     = s.adSet.endDate
            dailyBudget = s.adSet.dailyBudget
            billingType = s.adSet.billingType
            status      = s.adSet.status
        }

        target.creative.apply {
            imageUrl   = s.creative.imageUrl
            logoUrl    = s.creative.logoUrl
            title      = s.creative.title
            subtitle   = s.creative.subtitle
            landingUrl = s.creative.landingUrl
            status     = s.creative.status
        }

        target.updatedAt = LocalDateTime.now()
    }

    /** DB → 도메인 매핑 추가 */
    fun toDomain(e: ScheduleEntity): Schedule =
        Schedule(
            campaign = Campaign(
                id = requireNotNull(e.campaign.campaignId),
                totalBudget = requireNotNull(e.campaign.totalBudget),
                totalSpentBudget = 0L // 소진액은 Redis에서 관리
            ),
            adSet = AdSet(
                id = requireNotNull(e.adSet.adSetId),
                startDate = requireNotNull(e.adSet.startDate),
                endDate = requireNotNull(e.adSet.endDate),
                dailyBudget = requireNotNull(e.adSet.dailyBudget),
                billingType = requireNotNull(e.adSet.billingType),
                status = requireNotNull(e.adSet.status),
                bidAmount = requireNotNull(e.adSet.bidAmount)
            ),
            creative = Creative(
                id = requireNotNull(e.creative.creativeId),
                imageUrl = requireNotNull(e.creative.imageUrl),
                logoUrl = requireNotNull(e.creative.logoUrl),
                title = requireNotNull(e.creative.title),
                subtitle = e.creative.subtitle,
                landingUrl = requireNotNull(e.creative.landingUrl),
                status = requireNotNull(e.creative.status)
            )
        )
}