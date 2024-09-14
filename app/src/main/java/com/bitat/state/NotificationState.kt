package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.po.NoticeMsgPo
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    var id: Long = 0,
    var userId: Long = 0,
    var kind: Int = 0,
    var sourceId: Long = 0,
    var fromId: Long = 0,
    var time: Long = 0,
    var content: String = "",
    var displayContent: String = "",
    var comment: String = "",
    var commentId: Long = 0,
    var nickname: String = "",
    var profile: String = "",
    var cover: String = ""
)

data class NotificationState(
    val notifications: SnapshotStateList<Notification> = mutableStateListOf()
)