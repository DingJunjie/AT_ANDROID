package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.po.SingleMsgPo
import kotlinx.serialization.Serializable

/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
data class ChatDetailsState(
    val messageList: SnapshotStateList<SingleMsgPo> = mutableStateListOf(),
    val currentOptMsg: SingleMsgPo? = null,
    val currentPage: Int = 0,
)

@Serializable
class VideoMessageParams(
    val cover: String = "",
    val video: String = ""
)