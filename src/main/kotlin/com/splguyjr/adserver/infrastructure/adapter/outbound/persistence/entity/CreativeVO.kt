package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import com.splguyjr.adserver.domain.model.enum.Status
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class CreativeVO(

    @Column(table = "creative", name = "creative_id", nullable = false)
    var creativeId: Long? = null,

    @Column(table = "creative", name = "image_path", nullable = false, length = 2048)
    var imageUrl: String? = null,

    @Column(table = "creative", name = "logo_path", nullable = false, length = 2048)
    var logoUrl: String? = null,

    @Column(table = "creative", name = "title", nullable = false, length = 255)
    var title: String? = null,

    @Column(table = "creative", name = "subtitle", length = 255)
    var subtitle: String? = null,

    @Column(table = "creative", name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(table = "creative", name = "landing_url", nullable = false, length = 2048)
    var landingUrl: String? = null,

    @Column(table = "creative", name = "status", nullable = false, length = 5)
    var status: Status? = null
)


