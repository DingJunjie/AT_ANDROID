package com.bitat.repository.dto.req

data class NoticeReqDto(
    val fromId: Long,
    val kind: Int,
    val sourceId: Long
)