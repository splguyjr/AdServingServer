package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.SecondaryTable
import jakarta.persistence.SecondaryTables
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "schedule")
@SecondaryTables(
    value = [
        SecondaryTable(
            name = "campaign",
            pkJoinColumns = [PrimaryKeyJoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")]
        ),
        SecondaryTable(
            name = "ad_set",
            pkJoinColumns = [PrimaryKeyJoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")]
        ),
        SecondaryTable(
            name = "creative",
            pkJoinColumns = [PrimaryKeyJoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")]
        )
    ]
)
class ScheduleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    var id: Long? = null,

    @Embedded
    var campaign: CampaignVO,

    @Embedded
    var adSet: AdSetVO,

    @Embedded
    var creative: CreativeVO,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)