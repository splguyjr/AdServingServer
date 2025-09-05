package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.domain.model.AdSet
import com.splguyjr.adserver.domain.model.Campaign
import com.splguyjr.adserver.domain.model.Creative
import com.splguyjr.adserver.domain.model.Schedule
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.port.outbound.AdPlatformClientPort
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ScheduleSyncServiceTest {

    @Mock
    lateinit var client: AdPlatformClientPort
    @Mock
    lateinit var repo: ScheduleRepository

    @InjectMocks
    lateinit var sut: ScheduleSyncService

    private fun schedule(): Schedule = Schedule(
        campaign = Campaign(id = 1, totalBudget = 1000),
        adSet = AdSet(
            id = 2,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(1),
            dailyBudget = 100,
            billingType = BillingType.CPC,
            status = Status.ON
        ),
        creative = Creative(
            id = 3,
            imageUrl = "https://img",
            logoUrl = "https://logo",
            title = "t",
            subtitle = null,
            landingUrl = "https://landing",
            status = Status.ON
        )
    )

    @Test
    fun sync_returns_0_when_nothing_fetched() {
        whenever(client.fetchSchedules()).thenReturn(emptyList())

        val n = sut.sync()

        assertEquals(0, n)
        verify(repo, never()).saveOrUpdateAll(any())
    }

    @Test
    fun sync_saves_all_and_returns_count() {
        val list = listOf(schedule(), schedule())
        whenever(client.fetchSchedules()).thenReturn(list)
        whenever(repo.saveOrUpdateAll(list)).thenReturn(list.size)

        val n = sut.sync()

        assertEquals(list.size, n)
        verify(repo).saveOrUpdateAll(list)
    }
}