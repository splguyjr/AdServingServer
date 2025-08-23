package com.splguyjr.adserver.infrastructure.adapter.outbound.cache

import java.time.Duration

object RedisTtl {
    /** 후보 풀(Set) 및 값 기본 TTL */
    val default: Duration = Duration.ofMinutes(15)
}