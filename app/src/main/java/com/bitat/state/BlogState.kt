package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto

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

enum class BlogLoad {
    Success, Fail, NoData, TimeOut,Default
}

// TODO 认识 SnapShotStateList
data class BlogState(
    val blogList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val currentMenu: BlogMenuOptions = BlogMenuOptions.Recommend,
    val hasMore: Boolean = true,
    val updating: Boolean = false,
    val currentBlog: BlogBaseDto? = null,
    val topBarShow: Boolean = true,
    val flag: Int = 0,
    val isFirst: Boolean = true,
    val isLoadMore: Boolean = false,
    val loadResp: BlogLoad = BlogLoad.Default,
    val currentListIndex:Int=0,
    val listOffset:Int=0 ,//列表当前位置
    val timeLineList: SnapshotStateList<BlogBaseDto> = mutableStateListOf()
)


