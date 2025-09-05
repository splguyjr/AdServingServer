package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence

import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.ScheduleEntity
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper.ScheduleEntityMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.SpringDataScheduleRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ScheduleRepositoryAdapter(
    private val jpa: SpringDataScheduleRepository,
    private val mapper: ScheduleEntityMapper
) : ScheduleRepository {

    override fun findIdByNaturalKey(
        campaignId: Long, adSetId: Long, creativeId: Long
    ): Long? = jpa.findOneByNaturalKey(campaignId, adSetId, creativeId)?.id

    @Transactional
    override fun saveOrUpdate(schedule: Schedule): Long {
        val existing: ScheduleEntity? = jpa.findOneByNaturalKey(
            schedule.campaign.id, schedule.adSet.id, schedule.creative.id
        )
        return if (existing == null) {
            jpa.save(mapper.toEntity(schedule)).id!!
        } else {
            mapper.applyDomain(existing, schedule)
            jpa.save(existing).id!!
        }
    }

    @Transactional
    override fun saveOrUpdateAll(schedules: List<Schedule>): Int {
        schedules.forEach { saveOrUpdate(it) }
        return schedules.size
    }
}