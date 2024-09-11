package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.po.NoticeMsgPo
import kotlinx.serialization.Serializable



data class NotificationState(
    val notifications: SnapshotStateList<NoticeMsgPo> = mutableStateListOf()
)