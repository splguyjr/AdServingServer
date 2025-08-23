package com.splguyjr.adserver.application.dto

import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.dto.CreativeCache

data class AdDeliveryResponse(
    val adSetId: Long,
    val creative: CreativeCache
)