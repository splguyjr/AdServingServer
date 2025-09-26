package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.CandidateWarmupResult
import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetPort
import com.splguyjr.adserver.domain.readmodel.EligibleScheduleBudget
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class CandidateWarmupServiceTest {

    @Mock
    lateinit var scheduleRepo: ScheduleRepository
    @Mock
    lateinit var budgetReader: SpentBudgetPort
    @Mock
    lateinit var candidateCache: CandidateCachePort

    @InjectMocks
    lateinit var sut: CandidateWarmupService

    @Test
    fun warmup_filters_and_overwrites_set() {
        val okId = 1L
        val badTotalId = 2L
        val badDailyId = 3L

        val rows = listOf<EligibleScheduleBudget>(
            EligibleScheduleBudget(scheduleId = okId,      campaignTotalBudget = 1_000L, adSetDailyBudget = 100L),
            EligibleScheduleBudget(scheduleId = badTotalId, campaignTotalBudget = 50L,    adSetDailyBudget = 100L),
            EligibleScheduleBudget(scheduleId = badDailyId, campaignTotalBudget = 1_000L, adSetDailyBudget = 10L)
        )

        whenever(scheduleRepo.findEligibleOnDateWithBudgets(any())).thenReturn(rows)
        whenever(budgetReader.get(okId)).thenReturn(SpentBudget(10, 1))
        whenever(budgetReader.get(badTotalId)).thenReturn(SpentBudget(100, 1))
        whenever(budgetReader.get(badDailyId)).thenReturn(SpentBudget(10, 20))
        doNothing().`when`(candidateCache).overwriteCandidateScheduleIds(any())

        val result: CandidateWarmupResult = sut.warmup()

        assertEquals(1, result.candidates)
        verify(candidateCache).overwriteCandidateScheduleIds(eq(setOf(okId)))
    }

    @Test
    fun warmup_empty_overwrites_empty() {
        whenever(scheduleRepo.findEligibleOnDateWithBudgets(any())).thenReturn(emptyList())
        doNothing().`when`(candidateCache).overwriteCandidateScheduleIds(emptySet())

        val result = sut.warmup()

        assertEquals(0, result.candidates)
        verify(candidateCache).overwriteCandidateScheduleIds(emptySet())
    }
}