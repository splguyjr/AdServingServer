package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.domain.model.*
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.port.outbound.cache.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BudgetServiceTest {

    private lateinit var candidateCache: StubCandidateCache
    private lateinit var totalPort: FakeTotalSpentPort
    private lateinit var dailyPort: FakeDailySpentPort
    private lateinit var scheduleCache: FakeScheduleCache
    private lateinit var service: BudgetService

    @BeforeEach
    fun setup() {
        candidateCache = StubCandidateCache()
        totalPort = FakeTotalSpentPort()
        dailyPort = FakeDailySpentPort()
        scheduleCache = FakeScheduleCache()
        service = BudgetService(candidateCache, totalPort, dailyPort, scheduleCache)
    }

    // ------------------------------------------------------------------------
    // resetDailySpentForAll 테스트
    // ------------------------------------------------------------------------

    @Test
    fun resetDailySpentForAll_resets_only_nonzero_budgets() {
        // given
        candidateCache.ids = setOf(1L, 2L, 3L)
        dailyPort.dailyMap = mutableMapOf(
            1L to 100L,
            2L to 0L,
            3L to 50L
        )
        scheduleCache.put(1L, dummySchedule(1L, dailySpent = 100L))
        scheduleCache.put(3L, dummySchedule(3L, dailySpent = 50L))

        // when
        val count = service.resetDailySpentForAll()

        // then
        assertThat(count).isEqualTo(2) // 1,3만 reset됨
        assertThat(dailyPort.dailyMap.values).allMatch { it == 0L }
        assertThat(scheduleCache.get(1L)!!.adSet.dailySpentBudget).isEqualTo(0L)
        assertThat(scheduleCache.get(3L)!!.adSet.dailySpentBudget).isEqualTo(0L)
    }

    // ------------------------------------------------------------------------
    // applyCharge 테스트
    // ------------------------------------------------------------------------

    @Test
    fun applyCharge_increments_total_and_daily_budgets() {
        // given
        val schId = 1L
        val schedule = dummySchedule(schId, bid = 10L)
        totalPort.totalMap[schId] = 100L
        dailyPort.dailyMap[schId] = 20L

        // when
        val spent = service.applyCharge(schId, schedule)

        // then
        assertThat(spent.totalSpentBudget).isEqualTo(110L)
        assertThat(spent.dailySpentBudget).isEqualTo(30L)
    }

    // ------------------------------------------------------------------------
    // isEligibleToday 테스트
    // ------------------------------------------------------------------------

    @Test
    fun isEligibleToday_returns_false_when_budget_exceeded() {
        // given
        val schId = 1L
        val schedule = dummySchedule(schId, bid = 10L).copy(
            campaign = Campaign(id = schId, totalBudget = 100, totalSpentBudget = 0)
        )
        totalPort.totalMap[schId] = 95L
        dailyPort.dailyMap[schId] = 50L // daily는 여유 있지만 total 초과 예정

        // when
        val result = service.isEligibleToday(schId, schedule, LocalDate.now())

        // then
        assertThat(result).isFalse() // 95 + 10 > 100
    }

    @Test
    fun isEligibleToday_returns_true_when_within_budget() {
        val schId = 2L
        val schedule = dummySchedule(schId, bid = 10L).copy(
            campaign = Campaign(id = schId, totalBudget = 1000, totalSpentBudget = 0)
        )
        totalPort.totalMap[schId] = 100L
        dailyPort.dailyMap[schId] = 50L

        val result = service.isEligibleToday(schId, schedule, LocalDate.now())
        assertThat(result).isTrue()
    }

    // ------------------------------------------------------------------------
    // 🔧 Stub/Fake implementations
    // ------------------------------------------------------------------------

    private class StubCandidateCache : CandidateCachePort {
        var ids: Set<Long> = emptySet()
        override fun getCurrentCandidateScheduleIds(): Set<Long> = ids
        override fun overwriteCandidateScheduleIds(newIds: Set<Long>) { ids = newIds }
    }

    private class FakeScheduleCache : ScheduleCachePort {
        private val store = mutableMapOf<Long, Schedule>()
        override fun put(scheduleId: Long, schedule: Schedule) { store[scheduleId] = schedule }
        override fun get(scheduleId: Long): Schedule? = store[scheduleId]
        override fun delete(scheduleId: Long) { store.remove(scheduleId) }
    }

    private class FakeTotalSpentPort : TotalSpentPort {
        val totalMap = mutableMapOf<Long, Long>()
        override fun getTotal(scheduleId: Long): Long? = totalMap[scheduleId]
        override fun initTotalIfAbsent(scheduleId: Long) { totalMap.putIfAbsent(scheduleId, 0L) }
        override fun incrTotal(scheduleId: Long, delta: Long): Long {
            val newVal = (totalMap[scheduleId] ?: 0L) + delta
            totalMap[scheduleId] = newVal
            return newVal
        }
    }

    private class FakeDailySpentPort : DailySpentPort {
        var dailyMap = mutableMapOf<Long, Long>()
        override fun getDaily(scheduleId: Long): Long? = dailyMap[scheduleId]
        override fun initDailyIfAbsent(scheduleId: Long) { dailyMap.putIfAbsent(scheduleId, 0L) }
        override fun incrDaily(scheduleId: Long, delta: Long): Long {
            val newVal = (dailyMap[scheduleId] ?: 0L) + delta
            dailyMap[scheduleId] = newVal
            return newVal
        }
        override fun resetDailyToZero(scheduleId: Long): Long {
            val prev = dailyMap[scheduleId] ?: 0L
            dailyMap[scheduleId] = 0L
            return prev
        }
    }

    // ------------------------------------------------------------------------
    // 🧩 헬퍼
    // ------------------------------------------------------------------------

    private fun dummySchedule(id: Long, bid: Long = 10L, dailySpent: Long = 0L) = Schedule(
        campaign = Campaign(
            id = id,
            totalBudget = 1000,
            totalSpentBudget = 0
        ),
        adSet = AdSet(
            id = id,
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now().plusDays(1),
            dailyBudget = 100,
            billingType = BillingType.CPC,
            status = Status.ON,
            dailySpentBudget = dailySpent,
            bidAmount = bid
        ),
        creative = Creative(
            id = id,
            imageUrl = "image_$id.jpg",
            logoUrl = "logo_$id.jpg",
            title = "Title $id",
            subtitle = null,
            landingUrl = "https://landing",
            status = Status.ON
        )
    )
}
