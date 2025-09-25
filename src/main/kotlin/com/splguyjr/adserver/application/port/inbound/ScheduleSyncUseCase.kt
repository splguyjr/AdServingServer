package com.splguyjr.adserver.application.port.inbound

import com.splguyjr.adserver.presentation.dto.request.ScheduleDto

interface ScheduleSyncUseCase {
    fun upsertAll(schedules: List<ScheduleDto>)
}