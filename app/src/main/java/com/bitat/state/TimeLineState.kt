package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.resp.BlogBaseDto

/**
 *    author : shilu
 *    date   : 2024/8/27  18:13
 *    desc   :
 */
data class TimeLineState(
    val timeLineList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(), val isFirst: Boolean = true,
    val isLoadMore: Boolean = false,
    val loadResp: HttpLoadState = HttpLoadState.Default,
    val flag: Int = 0,
    val currentBlog: BlogBaseDto? = null,
    val updating: Boolean = false,
)