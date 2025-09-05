package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.`in`.ScheduleSyncUseCase
import com.splguyjr.adserver.domain.port.outbound.AdPlatformClientPort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScheduleSyncService(
    private val client: AdPlatformClientPort,
    private val scheduleRepository: ScheduleRepository
) : ScheduleSyncUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun sync(): Int {
        val schedules = client.fetchSchedules()

        if (schedules.isEmpty()) {
            log.info("ScheduleSync: no schedules fetched.")
            return 0
        }

        val n = scheduleRepository.saveOrUpdateAll(schedules)
        log.info("ScheduleSync: upserted={}", n)
        return n
    }
}