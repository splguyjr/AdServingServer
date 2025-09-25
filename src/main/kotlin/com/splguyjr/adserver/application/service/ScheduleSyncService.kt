package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.mapper.ScheduleDtoMapper
import com.splguyjr.adserver.application.port.inbound.ScheduleSyncUseCase
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.presentation.dto.request.ScheduleDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScheduleSyncService(
    private val scheduleRepository: ScheduleRepository,
    private val mapper: ScheduleDtoMapper
) : ScheduleSyncUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun upsertAll(schedules: List<ScheduleDto>) {
        // dto -> domain 모델로 변환
        val schduleList = mapper.toDomainList(schedules)

        // domain 계층의 인터페이스 호출하여 db에 저장
        val count = scheduleRepository.saveOrUpdateAll(schduleList)

        log.info("Schedules upsertAll completed. count={}", count)
    }
}