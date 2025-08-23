package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.id

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class AdSetCreativeId(
    val adsetId: Long = 0,
    val creativeId: Long = 0
) : Serializable    // 복합키는 반드시 직렬화 가능해야 함
