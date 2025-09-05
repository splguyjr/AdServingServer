package com.splguyjr.adserver.infrastructure.adapter.outbound.client

import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.AdPlatformClientPort
import com.splguyjr.adserver.infrastructure.adapter.outbound.client.feign.AdPlatformFeignClient
import com.splguyjr.adserver.infrastructure.adapter.outbound.client.mapper.ScheduleDtoMapper
import org.springframework.stereotype.Component

@Component
class AdPlatformClientAdapter(
    private val feign: AdPlatformFeignClient,
    private val mapper: ScheduleDtoMapper
) : AdPlatformClientPort {

    override fun fetchSchedules(): List<Schedule> {
       val rows = feign.schedules(null)
        return mapper.toDomainList(rows)
    }
}