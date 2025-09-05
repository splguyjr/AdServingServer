package com.splguyjr.adserver.domain.model

import com.splguyjr.adserver.domain.model.enum.Status

data class Creative(
    val id: Long,
    val imageUrl: String,
    val logoUrl: String,
    val title: String,
    val subtitle: String?,
    val landingUrl: String,
    val status: Status
) {
    init {
        require(landingUrl.isNotBlank()) { "landingUrl은 비어 있을 수 없습니다." }
        require(imageUrl.isNotBlank() || title.isNotBlank()) {
            "최소한 이미지나 타이틀 중 하나는 제공되어야 합니다."
        }
    }

    fun isActive(): Boolean = status == Status.ON
}