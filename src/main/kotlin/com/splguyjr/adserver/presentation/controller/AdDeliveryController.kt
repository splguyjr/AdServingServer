package com.splguyjr.adserver.presentation.controller

import com.splguyjr.adserver.application.service.AdDeliveryService
import com.splguyjr.adserver.presentation.dto.AdDeliveryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ads")
class AdDeliveryController(
    private val adDeliveryService: AdDeliveryService
) {
    @GetMapping
    fun deliverOne(): ResponseEntity<AdDeliveryResponse> {
        val response = adDeliveryService.deliverOne()
        return response?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }
}