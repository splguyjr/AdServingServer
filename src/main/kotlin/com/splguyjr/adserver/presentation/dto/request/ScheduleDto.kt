package com.splguyjr.adserver.presentation.dto.request

data class ScheduleDto(
    val campaign: CampaignDto,
    val adSet: AdSetDto,
    val creative: CreativeDto
)