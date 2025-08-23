package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.id.AdSetCreativeId
import jakarta.persistence.*

@Entity
@Table(
    name = "adset_creative",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_adset_creative", columnNames = ["adset_id", "creative_id"])
    ]
)
class AdSetCreativeJpa(
    @EmbeddedId
    val id: AdSetCreativeId,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("adsetId")
    @JoinColumn(name = "adset_id", nullable = false)
    val adSet: AdSetJpa,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("creativeId")
    @JoinColumn(name = "creative_id", nullable = false)
    val creative: CreativeJpa
)