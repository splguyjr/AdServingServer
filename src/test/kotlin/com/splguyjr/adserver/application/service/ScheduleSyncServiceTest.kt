package com.splguyjr.adserver.application.service

import com.splguyjr.adserver.application.mapper.ScheduleDtoMapper
import com.splguyjr.adserver.domain.model.*
import com.splguyjr.adserver.domain.model.enum.BillingType
import com.splguyjr.adserver.domain.model.enum.Status
import com.splguyjr.adserver.domain.port.outbound.ScheduleRepository
import com.splguyjr.adserver.presentation.dto.request.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ScheduleSyncServiceTest {

    private lateinit var repository: FakeScheduleRepository
    private lateinit var mapper: ScheduleDtoMapper
    private lateinit var service: ScheduleSyncService

    @BeforeEach
    fun setup() {
        repository = FakeScheduleRepository()
        mapper = ScheduleDtoMapper()
        service = ScheduleSyncService(repository, mapper)
    }

    /**
     * 여러 ScheduleDto를 upsertAll 호출 시, Repository에 모두 저장되는지 확인
     */
    @Test
    fun upsertAll_saves_all_schedules_in_repository() {
        val dtoList = listOf(
            dummyScheduleDto(1L),
            dummyScheduleDto(2L),
            dummyScheduleDto(3L)
        )

        service.upsertAll(dtoList)

        assertThat(repository.saved.size).isEqualTo(3)
        assertThat(repository.saved.keys).containsExactlyInAnyOrder(1L, 2L, 3L)
    }

    /**
     * 동일한 scheduleId가 들어오면 update가 수행되는지 확인
     */
    @Test
    fun upsertAll_updates_existing_schedules_when_ids_match() {
        val dto1 = dummyScheduleDto(1L)
        val dto2 = dummyScheduleDto(1L).copy(
            creative = dto1.creative.copy(title = "Updated Title")
        )

        // 첫 번째 저장 (insert)
        service.upsertAll(listOf(dto1))
        // 같은 ID로 업데이트 (update)
        service.upsertAll(listOf(dto2))

        val saved = repository.saved[1L]
        assertThat(saved!!.creative.title).isEqualTo("Updated Title")
    }

    // ------------------------------------------------------------------------
    // Fake Repository
    // ------------------------------------------------------------------------

    private class FakeScheduleRepository : ScheduleRepository {
        val saved = mutableMapOf<Long, Schedule>()

        override fun findIdByNaturalKey(campaignId: Long, adSetId: Long, creativeId: Long): Long? =
            saved.values.firstOrNull {
                it.campaign.id == campaignId &&
                        it.adSet.id == adSetId &&
                        it.creative.id == creativeId
            }?.campaign?.id

        override fun saveOrUpdate(schedule: Schedule): Long {
            val scheduleId = schedule.campaign.id
            saved[scheduleId] = schedule
            return scheduleId
        }

        override fun saveOrUpdateAll(schedules: List<Schedule>): Int {
            schedules.forEach { saveOrUpdate(it) }
            return schedules.size
        }

        override fun findById(id: Long): Schedule? = saved[id]

        override fun findEligibleOnDateWithBudgets(date: LocalDate) =
            emptyList<com.splguyjr.adserver.domain.readmodel.EligibleScheduleBudget>()
    }

    // ------------------------------------------------------------------------
    // 헬퍼 메서드
    // ------------------------------------------------------------------------

    /**
     * 테스트용 ScheduleDto 생성
     * - Campaign, AdSet, Creative가 동일 ID 기반으로 매핑됨
     * - totalBudget / totalSpentBudget 초기화 반영
     */
    private fun dummyScheduleDto(id: Long) = ScheduleDto(
        campaign = CampaignDto(
            id = id,
            totalBudget = 1000,
            totalSpentBudget = 100 // 예산 정보 초기 세팅
        ),
        adSet = AdSetDto(
            id = id * 2,
            campaignId = id,
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now().plusDays(1),
            dailyBudget = 200,
            billingType = BillingType.CPC.name,
            status = Status.ON.name,
            dailySpentBudget = 50L, // 일일 예산 소진액 초기값
            bidAmount = 10L
        ),
        creative = CreativeDto(
            id = id * 3,
            imageUrl = "image_$id.jpg",
            logoUrl = "logo_$id.jpg",
            title = "Title $id",
            subtitle = null,
            description = "Description $id",
            landingUrl = "https://landing/$id",
            status = Status.ON.name
        )
    )
}
