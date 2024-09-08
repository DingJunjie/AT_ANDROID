package com.bitat.state

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.utils.ConstBean

data class FeedbackViewState(
    val feedbackType: SnapshotStateList<ConstBean> = mutableStateListOf(),
    val feedbackContent: String = "",
    val imgs: SnapshotStateList<Uri> = mutableStateListOf(),
    val selectKind: Int = -1,
    val currentSize:Int=0,
    val imgsQiniu: ArrayList<String> = arrayListOf(),
    val canClick:Boolean=true
)
