package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.domain.model.*
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.domain.port.outbound.cache.CandidateCachePort
import com.splguyjr.adserver.domain.port.outbound.cache.ScheduleCachePort
import com.splguyjr.adserver.application.port.inbound.BudgetUseCase
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.readmodel.EligibleScheduleBudget
import com.splguyjr.adserver.domain.readmodel.SpentBudget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AdDeliveryServiceTest {

    private lateinit var candidateCache: StubCandidateCache
    private lateinit var scheduleCache: FakeScheduleCache
    private lateinit var scheduleRepo: FakeScheduleRepository
    private lateinit var budgetUseCase: StubBudgetUseCase
    private lateinit var service: AdDeliveryService

    @BeforeEach
    fun setup() {
        candidateCache = StubCandidateCache()
        scheduleCache = FakeScheduleCache()
        scheduleRepo = FakeScheduleRepository()
        budgetUseCase = StubBudgetUseCase()
        service = AdDeliveryService(candidateCache, scheduleCache, scheduleRepo, budgetUseCase)
    }

    /**
     * 후보군 캐시 hit + 적격 상태 → 캐시에서 반환
     */
    @Test
    fun deliverOne_returns_from_cache_when_candidate_hit_and_eligible() {
        val sch = dummySchedule(id = 1L)
        candidateCache.ids = setOf(1L)
        scheduleCache.put(1L, sch)
        budgetUseCase.eligibleIds = setOf(1L)

        val result = service.deliverOne()

        assertThat(result).isNotNull
        assertThat(result!!.scheduleId).isEqualTo(1L)
    }

    /**
     * 캐시 miss → DB에서 적격 스케줄 반환
     */
    @Test
    fun deliverOne_falls_back_to_repository_when_no_eligible_in_cache() {
        val sch1 = dummySchedule(id = 1L)
        scheduleRepo.saveOrUpdate(sch1)
        budgetUseCase.eligibleIds = setOf(1L)

        val result = service.deliverOne()

        assertThat(result).isNotNull
        assertThat(result!!.scheduleId).isEqualTo(1L)
    }

    /**
     * 모든 스케줄이 비적격 → null 반환
     */
    @Test
    fun deliverOne_returns_null_when_no_eligible_schedules_exist() {
        val sch = dummySchedule(id = 1L)
        candidateCache.ids = setOf(1L)
        scheduleCache.put(1L, sch)
        budgetUseCase.eligibleIds = emptySet()

        val result = service.deliverOne()

        assertThat(result).isNull()
    }

    // --------------------- [비적격 상세 케이스 테스트] ----------------------

    /**
     * 테스트: 기간이 오늘 포함 안 됨 → 비적격
     */
    @Test
    fun deliverOne_returns_null_when_schedule_out_of_date_range() {
        val sch = dummySchedule(id = 1L).copy(
            adSet = AdSet(
                id = 1L,
                startDate = LocalDate.now().plusDays(2),  // 내일부터 시작
                endDate = LocalDate.now().plusDays(5),
                dailyBudget = 100,
                billingType = BillingType.CPC,
                status = Status.ON,
                dailySpentBudget = 0L,
                bidAmount = 10L
            )
        )
        scheduleRepo.saveOrUpdate(sch)
        budgetUseCase.eligibleIds = setOf(1L)

        val result = service.deliverOne()

        assertThat(result).isNull()
    }

    /**
     * 테스트: 상태(Status)가 OFF → 비적격
     */
    @Test
    fun deliverOne_returns_null_when_schedule_status_is_off() {
        val sch = dummySchedule(id = 1L).copy(
            creative = dummySchedule(1L).creative.copy(status = Status.OFF)
        )
        scheduleRepo.saveOrUpdate(sch)
        budgetUseCase.eligibleIds = setOf(1L)

        val result = service.deliverOne()

        assertThat(result).isNull()
    }

    /**
     * 테스트: 총예산이 이미 한계에 도달했거나, 이번 서빙 금액을 더하면 초과되는 경우 → 비적격
     */
    @Test
    fun deliverOne_returns_null_when_total_budget_would_be_exceeded() {
        val sch = dummySchedule(id = 1L).copy(
            campaign = Campaign(
                id = 1L,
                totalBudget = 1000,
                totalSpentBudget = 995  // bidAmount 10 추가 시 1005 > 1000 → 초과
            ),
            adSet = dummySchedule(1L).adSet.copy(bidAmount = 10)
        )
        scheduleRepo.saveOrUpdate(sch)
        budgetUseCase.eligibleIds = setOf(1L)

        // 예산 캐시에도 같은 값 초기화
        budgetUseCase.totalSpentBudgets[1L] = 995L
        budgetUseCase.dailySpentBudgets[1L] = 0L

        val result = service.deliverOne()

        assertThat(result).isNull()
    }

    // ------------------------------------------------------------------------
    // 테스트 전용 Stub / Fake 클래스들
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

    private class FakeScheduleRepository : ScheduleRepository {
        private val store = mutableMapOf<Long, Schedule>()

        override fun findIdByNaturalKey(campaignId: Long, adSetId: Long, creativeId: Long): Long? =
            store.values.firstOrNull {
                it.campaign.id == campaignId && it.adSet.id == adSetId && it.creative.id == creativeId
            }?.campaign?.id

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

    private class StubBudgetUseCase : BudgetUseCase {
        var eligibleIds: Set<Long> = emptySet()
        val totalSpentBudgets = mutableMapOf<Long, Long>()
        val dailySpentBudgets = mutableMapOf<Long, Long>()

        override fun isEligibleToday(scheduleId: Long, schedule: Schedule, today: LocalDate): Boolean {
            if (!eligibleIds.contains(scheduleId)) return false

            val totalSpent = totalSpentBudgets[scheduleId] ?: schedule.campaign.totalSpentBudget
            val dailySpent = dailySpentBudgets[scheduleId] ?: schedule.adSet.dailySpentBudget
            val spent = SpentBudget(totalSpent, dailySpent)

            return schedule.isEligibleToday(today, spent)
        }

        override fun applyCharge(scheduleId: Long, schedule: Schedule): SpentBudget {
            val newTotal = (totalSpentBudgets[scheduleId] ?: 0L) + schedule.adSet.bidAmount
            val newDaily = (dailySpentBudgets[scheduleId] ?: 0L) + schedule.adSet.bidAmount
            totalSpentBudgets[scheduleId] = newTotal
            dailySpentBudgets[scheduleId] = newDaily
            return SpentBudget(newTotal, newDaily)
        }

        override fun resetDailySpentForAll(): Int = 0
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
