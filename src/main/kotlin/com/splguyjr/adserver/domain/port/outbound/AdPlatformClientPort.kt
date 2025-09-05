package com.splguyjr.adserver.domain.port.outbound

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import java.time.Instant

// 광고 플랫폼 서버로부터 필요한 정보들을 받아옴
interface AdPlatformClientPort {
    fun fetchCampaigns(since: Instant? = null): List<Campaign>
    fun fetchAdSets(campaignId: Long, since: Instant? = null): List<AdSet>
    fun fetchCreatives(adSetId: Long, since: Instant? = null): List<Creative>
}