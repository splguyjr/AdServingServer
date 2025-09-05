package com.splguyjr.adserver.infrastructure.adapter.outbound.client.feign

import com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto.ScheduleDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "adPlatform",
    url = "\${ad.platform.base-url}",
    configuration = [AdPlatformFeignConfig::class]
)
interface AdPlatformFeignClient {
    @GetMapping("/v1/schedules")
    fun schedules(@RequestParam(required = false) since: String? = null): List<ScheduleDto>
}