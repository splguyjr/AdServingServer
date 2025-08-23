package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository

import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.CreativeJpa
import org.springframework.data.jpa.repository.JpaRepository

interface CreativeRepository : JpaRepository<CreativeJpa, Long>