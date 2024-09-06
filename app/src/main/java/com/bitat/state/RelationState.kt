package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.UserPartDto

data class RelationState(
    val friendList: SnapshotStateList<UserPartDto> = mutableStateListOf()
)