package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.common.ResourceDto
import kotlinx.serialization.Serializable



data class NotificationState(
    val messageNotification: SnapshotStateList<Any> = mutableStateListOf()
)