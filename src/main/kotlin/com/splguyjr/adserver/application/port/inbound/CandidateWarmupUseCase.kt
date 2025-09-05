package com.splguyjr.adserver.application.port.inbound

data class CandidateWarmupResult(
    val candidates: Int
)

interface CandidateWarmupUseCase {
    fun warmup(): CandidateWarmupResult
}