package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto

data class ImagePreviewState(val imgList: SnapshotStateList<String> = mutableStateListOf())