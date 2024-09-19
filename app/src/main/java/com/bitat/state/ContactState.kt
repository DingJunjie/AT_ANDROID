package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.UserBase1Dto

/**
 *    author : shilu
 *    date   : 2024/9/19  17:20
 *    desc   :
 */
data class ContactState(val contactList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(), val flag: Int = 0)
