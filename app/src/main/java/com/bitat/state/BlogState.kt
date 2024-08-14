package com.bitat.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto

enum class BlogType {
    Recommend, Latest
}

// TODO 认识 SnapShotStateList
data class BlogState(
    val blogList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val type: BlogType = BlogType.Recommend,
    val hasMore: Boolean = true,
    val updating: Boolean = false,
    val currentBlog: BlogBaseDto? = null)


