package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogPartDto

/**
 *    author : shilu
 *    date   : 2024/9/5  10:07
 *    desc   :
 */
data class BrowserHistoryState(val currentTabIndex:Int=0,val myWorks: SnapshotStateList<BlogPartDto> = mutableStateListOf())
