package com.splguyjr.adserver.infrastructure.adapter.outbound.persistence

import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.cache.DailySpentPort
import com.splguyjr.adserver.domain.port.outbound.cache.ScheduleCachePort
import com.splguyjr.adserver.domain.port.outbound.cache.TotalSpentPort
import com.splguyjr.adserver.domain.readmodel.EligibleScheduleBudget
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.entity.ScheduleEntity
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.mapper.ScheduleEntityMapper
import com.splguyjr.adserver.infrastructure.adapter.outbound.persistence.repository.SpringDataScheduleRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ScheduleRepositoryAdapter(
    private val jpa: SpringDataScheduleRepository,
    private val mapper: ScheduleEntityMapper,
    private val scheduleWriter: ScheduleCachePort,
    private val totalWriter: TotalSpentPort,
    private val dailyWriter: DailySpentPort
) : ScheduleRepository {

    override fun findIdByNaturalKey(
        campaignId: Long, adSetId: Long, creativeId: Long
    ): Long? = jpa.findOneByNaturalKey(campaignId, adSetId, creativeId)?.id

    @Transactional
    override fun saveOrUpdate(schedule: Schedule): Long {
        val existing =
            jpa.findOneByNaturalKey(schedule.campaign.id, schedule.adSet.id, schedule.creative.id)
        return if (existing == null) insert(schedule) else update(existing, schedule)
    }

    @Transactional
    override fun saveOrUpdateAll(schedules: List<Schedule>): Int {
        schedules.forEach { saveOrUpdate(it) }
        return schedules.size
    }

    @Transactional(readOnly = true)
    override fun findEligibleOnDateWithBudgets(date: LocalDate): List<EligibleScheduleBudget> =
        jpa.findEligibleOnDateWithBudgets(date).map {
            EligibleScheduleBudget(
                scheduleId = it.getId(),
                campaignTotalBudget = it.getCampaignTotalBudget(),
                adSetDailyBudget = it.getAdSetDailyBudget()
            )
        }

    @Transactional(readOnly = true)
    override fun findById(id: Long): Schedule? =
        jpa.findById(id).orElse(null)?.let { mapper.toDomain(it) }

    /* -------- 내부 메소드 -------- */

    private fun insert(schedule: Schedule): Long {
        val saved = jpa.save(mapper.toEntity(schedule))
        val id = requireNotNull(saved.id)
        writeRedis(id, schedule)
        return id
    }

    private fun update(existing: ScheduleEntity, schedule: Schedule): Long {
        mapper.applyDomain(existing, schedule)
        val saved = jpa.save(existing)
        val id = requireNotNull(saved.id)
        writeRedis(id, schedule)
        return id
    }

    private fun writeRedis(scheduleId: Long, schedule: Schedule) {
        // 1) Schedule 캐시
        scheduleWriter.put(scheduleId, schedule)

        // 2) 예산 캐시: total/daily 분리 저장
        totalWriter.putTotal(scheduleId, schedule.campaign.totalSpentBudget)
        dailyWriter.putDaily(scheduleId, schedule.adSet.dailySpentBudget)
    }
}
