package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import com.splguyjr.adserver.domain.model.enum.Status
import jakarta.persistence.*

@Entity
@Table(name = "creative")
class CreativeJpa(
    @Id
    @Column(name = "creative_id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_adset_id")
    val defaultAdSet: AdSetJpa? = null,

    @Column(name = "image_path", nullable = false, length = 2048)
    var imagePath: String,

    @Column(name = "logo_path", nullable = false, length = 2048)
    var logoPath: String,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "subtitle", length = 255)
    var subtitle: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "landing_url", nullable = false, length = 2048)
    var landingUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 5)
    var status: Status
)