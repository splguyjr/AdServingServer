package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import com.splguyjr.adserver.domain.model.enum.Gender
import com.splguyjr.adserver.domain.model.enum.SegmentType
import jakarta.persistence.*

@Entity
@Table(name = "segment")
class SegmentJpa(
    @Id
    @Column(name = "segment_id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adset_id", nullable = false)
    val adSet: AdSetJpa,

    @Enumerated(EnumType.STRING)
    @Column(name = "segment_type", nullable = false, length = 10)
    var segmentType: SegmentType,

    @Column(name = "min_age", nullable = false)
    var minAge: Int? = null,

    @Column(name = "max_age", nullable = false)
    var maxAge: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 1)
    var gender: Gender
)