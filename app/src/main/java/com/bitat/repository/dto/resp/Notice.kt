package com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable

@Serializable
class NoticeDto {
    val fromId: Long = 0
    val kind: Int = 0
    val sourceId: Long = 0
    val nickname: String = ""
    val profile: String = ""
    val content: String = ""
    val cover: String = ""
}