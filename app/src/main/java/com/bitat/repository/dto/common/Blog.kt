package com.bitat.repository.dto.common

import kotlinx.serialization.Serializable

@Serializable
class TagsDto {
    var v0: Long = 0
    var v1: String = ""
}

@Serializable
class TopBlogsDto(
    var blogId: Long,
    var cover: String
)