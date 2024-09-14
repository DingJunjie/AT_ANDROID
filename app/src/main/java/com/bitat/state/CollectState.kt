package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.CollectPartDto

enum class CollectionTabs {
    Works, Custom, Music;

    fun getUiContent(): String {
        return when (this) {
            Works -> "作品"
            Custom -> "自定义收藏夹"
            Music -> "音乐"
        }
    }
}

data class CollectState(
    val collections: SnapshotStateList<CollectPartDto> = mutableStateListOf(),
    val currentCollectionItems: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val currentCollection: CollectPartDto = CollectPartDto(),
    val currentBlog: BlogBaseDto? = null,
    val currentTab: CollectionTabs = CollectionTabs.Works,
    val httpState: HttpLoadState = HttpLoadState.Default,
    val isReq: Boolean=false, //是否正在请求
    val footShow:Boolean=false
)