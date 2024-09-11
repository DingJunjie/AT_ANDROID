package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.UserBase1Dto

data class BlackListState(
    val myBlackList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    var flag: Int = 0,
    var currentUser: UserBase1Dto?=null
)
