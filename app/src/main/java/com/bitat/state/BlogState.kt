package com.bitat.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto

enum class BlogMenuOptions : MenuOptions {
    Recommend, Latest, Followed;

    override fun getUiContent(): String {
        return when (this) {
            Recommend ->
                "推荐"

            Latest ->
                "最新"

            Followed ->
                "关注"
        }
    }
}

enum class BlogOperation {
    Comment, At, Like, Collect, None
}

// TODO 认识 SnapShotStateList
data class BlogState(
    val blogList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val currentMenu: BlogMenuOptions = BlogMenuOptions.Recommend,
    val hasMore: Boolean = true,
    val updating: Boolean = false,
    val currentBlog: BlogBaseDto? = null,
    val topBarShow:Boolean=true
)


