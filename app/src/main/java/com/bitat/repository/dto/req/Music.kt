package com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class MusicFind(
    var pageSize: Int
)

@Serializable
class MusicCollect(
    var musicId: Long,
    var ops: Byte //收否收藏，具体看常量
)

@Serializable
class GetCollect(
    var lastTime: Long,
    var pageSize: Int
)

@Serializable
class SearchMusic(
    var searchWord: String,
    var pageSize: Int,
    var pageNo: Int
)

@Serializable
class FindMusicBlog(
    var musicId: Long,
    var pageSize: Int,
    var pageNo: Int
)

@Serializable
class MusicGet(
    var musicId: Long,
)