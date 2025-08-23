package com.splguyjr.adserver.domain.port.outbound

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Segment

interface AdRepositoryPort {
    fun upsertCampaign(campaign: Campaign)
    fun upsertAdSet(adSet: AdSet)
    fun upsertCreative(creative: Creative)
    fun upsertAdSetCreative(adSetId: Long, creativeId: Long)
    fun upsertSegment(segment: Segment)
}