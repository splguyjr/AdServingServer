package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.port.inbound.CandidateWarmupResult
import com.splguyjr.adserver.domain.model.*
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.cache.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.cache.DailySpentPort
import com.splguyjr.adserver.domain.port.outbound.cache.TotalSpentPort
import com.splguyjr.adserver.domain.readmodel.EligibleScheduleBudget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CandidateWarmupServiceTest {

    private lateinit var scheduleRepo: FakeScheduleRepository
    private lateinit var totalReader: StubTotalSpentPort
    private lateinit var dailyReader: StubDailySpentPort
    private lateinit var candidateCache: StubCandidateCache
    private lateinit var service: CandidateWarmupService

    @BeforeEach
    fun setup() {
        scheduleRepo = FakeScheduleRepository()
        totalReader = StubTotalSpentPort()
        dailyReader = StubDailySpentPort()
        candidateCache = StubCandidateCache()
        service = CandidateWarmupService(scheduleRepo, totalReader, dailyReader, candidateCache)
    }

    /**
     * 조건 1: 상태/기간 적격 + 예산 여유가 있는 스케줄만 후보군에 포함된다.
     */
    @Test
    fun warmup_stores_only_budget_eligible_candidates() {
        val today = LocalDate.now()
        val sch1 = dummySchedule(1L) // 총예산/일예산 여유 있음
        val sch2 = dummySchedule(2L) // 총예산 초과
        val sch3 = dummySchedule(3L) // 일예산 초과

        scheduleRepo.saveOrUpdateAll(listOf(sch1, sch2, sch3))

        // Redis에 현재 예산 상태 반영
        totalReader.data[1L] = 500L  // totalBudget = 1000, OK
        totalReader.data[2L] = 1200L // totalBudget = 1000, 초과
        totalReader.data[3L] = 300L  // totalBudget = 1000, OK

        dailyReader.data[1L] = 50L   // dailyBudget = 100, OK
        dailyReader.data[2L] = 20L   // dailyBudget = 100, OK
        dailyReader.data[3L] = 150L  // dailyBudget = 100, 초과

        val result: CandidateWarmupResult = service.warmup()

        assertThat(result.candidates).isEqualTo(1) // sch1만 통과
        assertThat(candidateCache.ids).containsExactly(1L)
    }

    /**
     * 조건 2: 모든 스케줄이 총예산/일예산을 초과한 경우 후보군은 비어 있다.
     */
    @Test
    fun warmup_returns_zero_when_all_schedules_exceed_budget() {
        val sch1 = dummySchedule(1L)
        val sch2 = dummySchedule(2L)
        scheduleRepo.saveOrUpdateAll(listOf(sch1, sch2))

        // db에 저장된 더미 스케줄 일일 예산 100, 총 예산 1000인 상황에서 redis 예산 초과 설정
        totalReader.data[1L] = 1000L
        dailyReader.data[1L] = 100L
        totalReader.data[2L] = 1200L
        dailyReader.data[2L] = 200L

        val result = service.warmup()

        assertThat(result.candidates).isEqualTo(0)
        assertThat(candidateCache.ids).isEmpty()
    }

    // ------------------------------------------------------------------------
    // 테스트용 Stub / Fake 구현체
    // ------------------------------------------------------------------------

    private class FakeScheduleRepository : ScheduleRepository {
        private val store = mutableMapOf<Long, Schedule>()

        override fun findIdByNaturalKey(campaignId: Long, adSetId: Long, creativeId: Long): Long? = null

        override fun saveOrUpdate(schedule: Schedule): Long {
            store[schedule.campaign.id] = schedule
            return schedule.campaign.id
        }

        override fun saveOrUpdateAll(schedules: List<Schedule>): Int {
            schedules.forEach { saveOrUpdate(it) }
            return schedules.size
        }

        override fun findById(id: Long): Schedule? = store[id]

        override fun findEligibleOnDateWithBudgets(date: LocalDate): List<EligibleScheduleBudget> {
            return store.values
                .filter { it.creative.isActive() && it.adSet.isActiveOn(date) }
                .map {
                    EligibleScheduleBudget(
                        scheduleId = it.campaign.id,
                        campaignTotalBudget = it.campaign.totalBudget,
                        adSetDailyBudget = it.adSet.dailyBudget
                    )
                }
        }
    }

    private class StubCandidateCache : CandidateCachePort {
        var ids: Set<Long> = emptySet()
        override fun getCurrentCandidateScheduleIds(): Set<Long> = ids
        override fun overwriteCandidateScheduleIds(newIds: Set<Long>) { ids = newIds }
    }

    private class StubTotalSpentPort : TotalSpentPort {
        var data: MutableMap<Long, Long> = mutableMapOf()

        override fun getTotal(scheduleId: Long): Long? = data[scheduleId]

        override fun initTotalIfAbsent(scheduleId: Long) {
            data.putIfAbsent(scheduleId, 0L)
        }

        override fun incrTotal(scheduleId: Long, delta: Long): Long {
            val newValue = (data[scheduleId] ?: 0L) + delta
            data[scheduleId] = newValue
            return newValue
        }
    }

    private class StubDailySpentPort : DailySpentPort {
        var data: MutableMap<Long, Long> = mutableMapOf()

        override fun getDaily(scheduleId: Long): Long? = data[scheduleId]

        override fun initDailyIfAbsent(scheduleId: Long) {
            data.putIfAbsent(scheduleId, 0L)
        }

        override fun incrDaily(scheduleId: Long, delta: Long): Long {
            val newValue = (data[scheduleId] ?: 0L) + delta
            data[scheduleId] = newValue
            return newValue
        }

        override fun resetDailyToZero(scheduleId: Long): Long {
            data[scheduleId] = 0L
            return 0L
        }
    }

    // ------------------------------------------------------------------------
    // 헬퍼 메서드
    // ------------------------------------------------------------------------

    private fun dummySchedule(id: Long) = Schedule(
        campaign = Campaign(id, totalBudget = 1000, totalSpentBudget = 0),
        adSet = AdSet(
            id = id,
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now().plusDays(1),
            dailyBudget = 100,
            billingType = BillingType.CPC,
            status = Status.ON,
            dailySpentBudget = 0L,
            bidAmount = 10L
        ),
        creative = Creative(
            id = id,
            imageUrl = "image.jpg",
            logoUrl = "logo.jpg",
            title = "Title",
            subtitle = null,
            landingUrl = "https://landing",
            status = Status.ON
        )
    )
}
