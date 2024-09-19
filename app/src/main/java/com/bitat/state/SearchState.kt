package com.bitat.state

import android.app.appsearch.SearchResults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.po.SearchHistoryPo

enum class SearchType {
    BLOG, VIDEO,

    //    ACTIVITY, PODCAST,
    USER;
//    TAG;


    companion object {
        fun getUiContent(type: SearchType): String {
            return when (type) {
                BLOG -> "综合"
                VIDEO -> "视频"
                USER -> "用户"
//                PODCAST -> "播客"
//                ACTIVITY -> "活动"
//                TAG -> "话题"
            }
        }
    }
}

data class SearchState(
    val keyword: String = "",
    val historyList: SnapshotStateList<SearchHistoryPo> = mutableStateListOf(),
    val searchBlogResult: SnapshotStateList<SearchResults> = mutableStateListOf(),
    val searchType: SearchType = SearchType.BLOG,
    val searchUserResult: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    val searchVideoResult: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val searchResult: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val rankingList: SnapshotStateList<BlogPartDto> = mutableStateListOf(), val flag: Int = 0
)