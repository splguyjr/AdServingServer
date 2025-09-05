package com.splguyjr.adserver.infrastructure.adapter.outbound.client.dto

data class ScheduleDto(
    val campaign: CampaignDto,
    val adSet: AdSetDto,
    val creative: CreativeDto
)