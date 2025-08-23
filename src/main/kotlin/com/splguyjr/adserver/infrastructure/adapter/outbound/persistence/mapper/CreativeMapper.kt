package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper

import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.AdSetJpa
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CreativeJpa

object CreativeMapper {
    fun toDomain(jpa: CreativeJpa) = Creative(
        id = jpa.id,
        defaultAdSetId = jpa.defaultAdSet?.id,
        imagePath = jpa.imagePath,
        logoPath = jpa.logoPath,
        title = jpa.title,
        subtitle = jpa.subtitle,
        description = jpa.description,
        landingUrl = jpa.landingUrl,
        status = jpa.status
    )

    fun toEntity(domain: Creative, defaultAdSet: AdSetJpa?) = CreativeJpa(
        id = domain.id,
        defaultAdSet = defaultAdSet,
        imagePath = domain.imagePath,
        logoPath = domain.logoPath,
        title = domain.title,
        subtitle = domain.subtitle,
        description = domain.description,
        landingUrl = domain.landingUrl,
        status = domain.status
    )
}