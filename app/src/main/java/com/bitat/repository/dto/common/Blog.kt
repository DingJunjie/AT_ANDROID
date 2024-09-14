package com.bitat.repository.dto.common

import com.bitat.repository.dto.resp.BlogTagDto
import kotlinx.serialization.Serializable

@Serializable
class TagsDto {
    var v0: Long = 0
    var v1: String = ""
}

fun TagsDto.toBlogTagDto(): BlogTagDto {
    val blogTagDto = BlogTagDto()
    blogTagDto.id = this.v0
    blogTagDto.name = this.v1
    return blogTagDto
}

@Serializable
class TopBlogsDto(var blogId: Long, var cover: String)