package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.UserBase1Dto

/**
 *    author : shilu
 *    date   : 2024/9/5  10:07
 *    desc   : 观看历史
 */
data class BrowserHistoryState(val currentTabIndex: Int = 0, val myWorks: SnapshotStateList<BlogPartDto> = mutableStateListOf(), val userList: SnapshotStateList<UserBase1Dto> = mutableStateListOf())
