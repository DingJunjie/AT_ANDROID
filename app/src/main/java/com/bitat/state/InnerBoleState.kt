package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto

/**
 *    author : shilu
 *    date   : 2024/8/13  18:21
 *    desc   :
 */
data class InnerBoleState(
    val blogList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val currentBlog: BlogBaseDto = BlogBaseDto(),
    val currentIndex: Int = 0,
)