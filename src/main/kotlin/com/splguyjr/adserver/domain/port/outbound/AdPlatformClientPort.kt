package com.splguyjr.adserver.domain.port.outbound

import com.splguyjr.adserver.domain.model.Schedule

// 광고 플랫폼 서버로부터 필요한 정보들을 받아옴
interface AdPlatformClientPort {
    fun fetchSchedules(): List<Schedule>
}