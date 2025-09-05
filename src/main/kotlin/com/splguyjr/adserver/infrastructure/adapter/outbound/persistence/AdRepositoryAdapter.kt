package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.port.outbound.AdRepositoryPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdRepositoryAdapter : AdRepositoryPort {

    @Transactional
    override fun upsertCampaign(campaign: Campaign) {
        // no-op: legacy campaign table removed; managed via Schedule + VOs
    }

    @Transactional
    override fun upsertAdSet(adSet: AdSet) {
        // no-op: legacy ad_set table removed; managed via Schedule + VOs
    }

    @Transactional
    override fun upsertCreative(creative: Creative) {
        // no-op: legacy creative table removed; managed via Schedule + VOs
    }

    @Transactional
    override fun upsertAdSetCreative(adSetId: Long, creativeId: Long) {
        // no-op: junction table removed; linkage represented within Schedule + VOs
    }

}