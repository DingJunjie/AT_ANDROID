package com.bitat.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import com.bitat.repository.dto.resp.UserHomeDto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.po.SingleRoomPo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ChatState(
    val chatList: SnapshotStateList<SingleRoomPo> = mutableStateListOf(),
    var currentRoom: SingleRoomPo = SingleRoomPo(),
    val currentUserInfo: UserHomeDto? = null,
    val flag: Int = 0
)
