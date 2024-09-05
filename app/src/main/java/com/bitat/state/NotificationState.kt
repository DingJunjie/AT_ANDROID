package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class NotificationState(
    val messageNotification: SnapshotStateList<Any> = mutableStateListOf()
)