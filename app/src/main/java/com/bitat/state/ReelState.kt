package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto


data class ReelState(val resList: SnapshotStateList<BlogBaseDto> = mutableStateListOf() ,//视频图片list
    val resIndex:Int=0,
    val flag:Int=0,
    val currentBlog: Any? = null,
    val blogPartList: SnapshotStateList<BlogPartDto> = mutableStateListOf() )
