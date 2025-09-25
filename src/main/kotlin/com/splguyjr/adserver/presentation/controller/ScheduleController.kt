package com.splguyjr.adserver.presentation.controller

import com.splguyjr.adserver.application.port.inbound.ScheduleSyncUseCase
import com.splguyjr.adserver.presentation.dto.request.ScheduleUpsertRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedules")
class ScheduleController(
    private val useCase: ScheduleSyncUseCase
) {
    @PostMapping
    fun upsertAll(@RequestBody req: ScheduleUpsertRequest): ResponseEntity<Void> {
        useCase.upsertAll(req.scheduleList)
        return ResponseEntity.ok().build()
    }
}
