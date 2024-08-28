package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto

/**
 *    author : shilu
 *    date   : 2024/8/27  18:13
 *    desc   :
 */
data class TimeLineState(val isUpdate:Boolean=false,val blogList: SnapshotStateList<BlogBaseDto> = mutableStateListOf() )