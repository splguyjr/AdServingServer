package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Segment
import com.splguyjr.adserver.domain.port.outbound.AdRepositoryPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetCreativeJpa
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.id.AdSetCreativeId
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper.AdSetMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper.CampaignMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper.CreativeMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper.SegmentMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdRepositoryAdapter(
    private val campaignRepo: CampaignRepository,
    private val adSetRepo: AdSetRepository,
    private val creativeRepo: CreativeRepository,
    private val adSetCreativeRepo: AdSetCreativeRepository,
    private val segmentRepo: SegmentRepository
) : AdRepositoryPort {

    @Transactional
    override fun upsertCampaign(campaign: Campaign) {
        val existing = campaignRepo.findById(campaign.id).orElse(null)

        if (existing == null) {
            campaignRepo.save(CampaignMapper.toEntity(campaign))
        } else {
            CampaignMapper.applyUpdates(existing, campaign)
        }
        campaignRepo.save(CampaignMapper.toEntity(campaign))
    }

    @Transactional
    override fun upsertAdSet(adSet: AdSet) {
        val campaign = campaignRepo.getReferenceById(adSet.campaignId)
        val existing = adSetRepo.findById(adSet.id).orElse(null)
        if (existing == null) {
            adSetRepo.save(AdSetMapper.toEntity(adSet, campaign))
        } else {
            AdSetMapper.applyUpdates(existing, adSet, campaign)
        }
    }

    @Transactional
    override fun upsertCreative(creative: Creative) {
        val defaultAdSet = creative.defaultAdSetId?.let { adSetRepo.findById(it).orElse(null) }
        creativeRepo.save(CreativeMapper.toEntity(creative, defaultAdSet))
    }

    @Transactional
    override fun upsertAdSetCreative(adSetId: Long, creativeId: Long) {
        val id = AdSetCreativeId(adsetId = adSetId, creativeId = creativeId)
        if (!adSetCreativeRepo.existsById(id)) {
            val adSet = adSetRepo.getReferenceById(adSetId)
            val creative = creativeRepo.getReferenceById(creativeId)
            adSetCreativeRepo.save(AdSetCreativeJpa(id = id, adSet = adSet, creative = creative))
        }
    }

    @Transactional
    override fun upsertSegment(segment: Segment) {
        val adSet = adSetRepo.getReferenceById(segment.adSetId)
        segmentRepo.save(SegmentMapper.toEntity(segment, adSet))
    }
}