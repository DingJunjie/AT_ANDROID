package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto


data class ReelState(val resList: SnapshotStateList<BlogBaseDto> = mutableStateListOf() ,//视频图片list
    val resIndex:Int=0,
    val flag:Int=0,
    val currentBlog: BlogBaseDto? = null,)
