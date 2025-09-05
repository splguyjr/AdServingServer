package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.port.outbound.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.SpentBudgetReaderPort
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import com.splguyjr.adserver.infrastructure.adapter.outbound.cache.ScheduleRedisCache
import com.splguyjr.adserver.presentation.dto.AdDeliveryResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*

import java.time.LocalDate
import kotlin.test.Test

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension::class)
class AdDeliveryServiceTest {

    @Mock
    lateinit var candidateCache: CandidateCachePort
    @Mock
    lateinit var scheduleCache: ScheduleRedisCache
    @Mock
    lateinit var scheduleRepository: ScheduleRepository
    @Mock
    lateinit var spentBudgetReader: SpentBudgetReaderPort

    @InjectMocks
    lateinit var sut: AdDeliveryService

    private fun scheduleActive(today: LocalDate) = Schedule(
        campaign = Campaign(id = 10, totalBudget = 1_000, totalSpentBudget = 0),
        adSet = AdSet(
            id = 20,
            startDate = today.minusDays(1),
            endDate = today.plusDays(1),
            dailyBudget = 100,
            billingType = BillingType.CPC,
            status = Status.ON,
            dailySpentBudget = 0
        ),
        creative = Creative(
            id = 30,
            imageUrl = "https://img",
            logoUrl = "https://logo",
            title = "title",
            subtitle = "sub",
            landingUrl = "https://landing",
            status = Status.ON
        )
    )

    @Test
    fun deliverOne_returns_from_cache_when_candidate_hit_and_eligible() {
        val today = LocalDate.now()
        val id = 111L
        val sch = scheduleActive(today)
        val spent = SpentBudget(totalSpentBudget = 10, dailySpentBudget = 1)

        whenever(candidateCache.getCurrentCandidateScheduleIds()).thenReturn(setOf(id))
        whenever(scheduleCache.get(id)).thenReturn(sch)
        whenever(spentBudgetReader.get(id)).thenReturn(spent)

        val res: AdDeliveryResponse? = sut.deliverOne()

        assertNotNull(res)
        assertEquals(id, res!!.scheduleId)
        assertEquals(sch.creative.imageUrl, res.imageUrl)

        verify(scheduleCache, times(1)).get(id)
        verify(scheduleRepository, never()).findById(any())
    }

    @Test
    fun deliverOne_returns_null_when_no_candidates_and_no_db_eligible() {
        whenever(candidateCache.getCurrentCandidateScheduleIds()).thenReturn(emptySet())
        whenever(scheduleRepository.findEligibleOnDateWithBudgets(any())).thenReturn(emptyList())

        val res = sut.deliverOne()

        assertNull(res)
        verify(scheduleRepository, times(1)).findEligibleOnDateWithBudgets(any())
    }

    @Test
    fun deliverOne_skips_candidate_when_dailySpent_exceeds_or_equals_dailyBudget_and_returns_null_on_no_fallback() {
        val today = LocalDate.now()
        val id = 222L
        val sch = scheduleActive(today) // dailyBudget = 100

        whenever(candidateCache.getCurrentCandidateScheduleIds()).thenReturn(setOf(id))
        whenever(scheduleCache.get(id)).thenReturn(sch)
        // 일일 소진액 == 일일 예산 (>= 이면 부적격)
        whenever(spentBudgetReader.get(id)).thenReturn(SpentBudget(totalSpentBudget = 10, dailySpentBudget = 100))
        // 폴백용 DB eligible 결과 없음
        whenever(scheduleRepository.findEligibleOnDateWithBudgets(any())).thenReturn(emptyList())

        val res = sut.deliverOne()

        assertNull(res)
        verify(scheduleCache, times(1)).get(id)
        verify(scheduleRepository, times(1)).findEligibleOnDateWithBudgets(any())
        verify(scheduleRepository, never()).findById(any())
    }

    @Test
    fun deliverOne_skips_candidate_when_totalSpent_exceeds_or_equals_totalBudget_and_returns_null_on_no_fallback() {
        val today = LocalDate.now()
        val id = 333L
        val sch = scheduleActive(today) // totalBudget = 1000

        whenever(candidateCache.getCurrentCandidateScheduleIds()).thenReturn(setOf(id))
        whenever(scheduleCache.get(id)).thenReturn(sch)
        // 누적 소진액 == 총 예산 (>= 이면 부적격)
        whenever(spentBudgetReader.get(id)).thenReturn(SpentBudget(totalSpentBudget = 1_000, dailySpentBudget = 0))
        // 폴백용 DB eligible 결과 없음
        whenever(scheduleRepository.findEligibleOnDateWithBudgets(any())).thenReturn(emptyList())

        val res = sut.deliverOne()

        assertNull(res)
        verify(scheduleCache, times(1)).get(id)
        verify(scheduleRepository, times(1)).findEligibleOnDateWithBudgets(any())
        verify(scheduleRepository, never()).findById(any())
    }
}