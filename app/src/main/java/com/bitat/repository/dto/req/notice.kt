package com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
data class NoticeReqDto(
    val fromId: Long,
    val kind: Int,
    val sourceId: Long
)