package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.po.SingleMsgPo

/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
data class ChatDetailsState(
    val messageList: SnapshotStateList<SingleMsgPo> = mutableStateListOf(),
    val currentOptMsg: SingleMsgPo? = null,
)