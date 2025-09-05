package com.splguyjr.adserver.infrastructure.adapter.outbound.client.feign

import com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto.AdSetDto
import com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto.CampaignDto
import com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto.CreativeDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "adPlatform",
    url = "\${ad.platform.base-url}",
    configuration = [AdPlatformFeignConfig::class]
)
interface AdPlatformFeignClient {
    @GetMapping("/v1/campaigns")
    fun campaigns(@RequestParam since: String? = null): List<CampaignDto>

    @GetMapping("/v1/campaigns/{campaignId}/adsets")
    fun adsets(@PathVariable campaignId: Long, @RequestParam since: String? = null): List<AdSetDto>

    @GetMapping("/v1/adsets/{adSetId}/creatives")
    fun creatives(@PathVariable adSetId: Long, @RequestParam since: String? = null): List<CreativeDto>
}