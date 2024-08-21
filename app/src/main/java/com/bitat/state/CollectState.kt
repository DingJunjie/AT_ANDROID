package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.CollectPartDto

data class CollectState(
    val collections: SnapshotStateList<CollectPartDto> = mutableStateListOf(),
    val currentCollection: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val currentCollectionId: Long = -1,
    val currentBlog: BlogBaseDto? = null
)