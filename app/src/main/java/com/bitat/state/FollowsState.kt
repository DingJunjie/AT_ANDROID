package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.resp.UserBase1Dto

/**
 *    author : shilu
 *    date   : 2024/9/12  17:27
 *    desc   :
 */
data class FollowsState(val followsList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(), //
    val flag: Int = 0, val isLoadMore: Boolean = false, //
    val loadResp: HttpLoadState = HttpLoadState.Default)
