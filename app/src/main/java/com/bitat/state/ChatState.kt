package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.UserHomeDto
import com.bitat.repository.po.SingleRoomPo

data class ChatState(
    val roomList: SnapshotStateList<SingleRoomPo> = mutableStateListOf(),
    var currentRoom: SingleRoomPo = SingleRoomPo(),
    val currentUserInfo: UserHomeDto? = null,
    val flag: Int = 0
)
