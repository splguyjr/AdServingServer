package com.splguyjr.adserver.presentation.controller

import com.splguyjr.adserver.application.dto.AdDeliveryResponse
import com.splguyjr.adserver.application.service.AdDeliveryService
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
        val delivered = adDeliveryService.deliverOne() ?: return ResponseEntity.noContent().build()
        return ResponseEntity.ok(AdDeliveryResponse(delivered.adSetId, delivered.creative))
    }
}