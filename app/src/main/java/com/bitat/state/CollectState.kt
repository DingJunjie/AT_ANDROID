package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.CollectPartDto

data class CollectState(
    val collections: SnapshotStateList<CollectPartDto> = mutableStateListOf(),
    val currentCollectionItems: SnapshotStateList<BlogPartDto> = mutableStateListOf(),
    val currentCollection: CollectPartDto? = null,
    val currentBlog: BlogBaseDto? = null,
)