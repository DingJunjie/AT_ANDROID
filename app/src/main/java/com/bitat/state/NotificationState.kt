package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.common.ResourceDto
import kotlinx.serialization.Serializable

@Serializable
data class SocialNotice(
    val blogId: Long,
    val commentId: Long,
    val comment: String,
    val resource: ResourceDto,
)

data class NotificationState(
    val messageNotification: SnapshotStateList<Any> = mutableStateListOf()
)