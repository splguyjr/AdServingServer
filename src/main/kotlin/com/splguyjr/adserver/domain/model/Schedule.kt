package com.splguyjr.adserver.domain.model

data class Schedule(
    val campaign: Campaign,
    val adSet: AdSet,
    val creative: Creative
)