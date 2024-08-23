package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    val currentCollectionItems: SnapshotStateList<BlogPartDto> = mutableStateListOf(),
    val currentCollection: CollectPartDto? = null,
    val currentBlog: BlogBaseDto? = null,
    val currentTab: CollectionTabs = CollectionTabs.Works,
)