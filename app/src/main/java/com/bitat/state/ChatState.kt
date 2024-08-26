package com.bitat.state

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

data class ChatState(
    val chatList: SnapshotStateMap<Long, MutableList<String>> = mutableStateMapOf(),
    val currentRoom: Long = -1,

)