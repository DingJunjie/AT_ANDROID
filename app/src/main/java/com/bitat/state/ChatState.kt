package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.po.RoomCfg
import com.bitat.repository.po.SingleRoomPo

data class ChatState(
    val chatList: SnapshotStateList<SingleRoomPo> = mutableStateListOf(),
    val currentRoom: SingleRoomPo? = null,
    val currentCfg: RoomCfg = RoomCfg(),
    val currentUserInfo: UserPartDto? = null
)