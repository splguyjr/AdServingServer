package com.splguyjr.adserver.infrastructure.adapter.outbound.client

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Segment
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.model.enum.SegmentType
import com.splguyjr.adserver.domain.port.outbound.AdPlatformClientPort
import com.splguyjr.adserver.domain.common.exception.enumOrThrow
import com.splguyjr.adserver.infrastructure.adapter.outbound.client.feign.AdPlatformFeignClient
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AdPlatformClientAdapter(
    private val feign: AdPlatformFeignClient
) : AdPlatformClientPort {

    override fun fetchCampaigns(since: Instant?): List<Campaign> =
        feign.campaigns(since?.toString()).map { Campaign(it.id, it.totalBudget) }

    override fun fetchAdSets(campaignId: Long, since: Instant?): List<AdSet> =
        feign.adsets(campaignId, since?.toString()).map {
            AdSet(
                id = it.id,
                campaignId = it.campaignId,
                startDate = it.startDate,
                endDate = it.endDate,
                dailyBudget = it.dailyBudget,
                bidAmount = it.bidAmount,
                billingType = enumOrThrow<BillingType>(it.billingType, "adset.billingType"),
                status     = enumOrThrow<Status>(it.status, "adset.status")
            )
        }

    override fun fetchCreatives(adSetId: Long, since: Instant?): List<Creative> =
        feign.creatives(adSetId, since?.toString()).map {
            Creative(
                id = it.id,
                defaultAdSetId = it.defaultAdSetId,
                imagePath = it.imagePath,
                logoPath = it.logoPath,
                title = it.title,
                subtitle = it.subtitle,
                description = it.description,
                landingUrl = it.landingUrl,
                status = enumOrThrow<Status>(it.status, "creative.status")
            )
        }

    override fun fetchSegments(adSetId: Long): List<Segment> =
        feign.segments(adSetId).map {
            Segment(
                id = it.id,
                adSetId = it.adSetId,
                segmentType = enumOrThrow<SegmentType>(it.segmentType, "segment.segmentType"),
                minAge = it.minAge,
                maxAge = it.maxAge,
                gender = enumOrThrow(it.gender, "segment.gender")
            )
        }
}