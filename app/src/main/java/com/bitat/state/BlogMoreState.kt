package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.utils.ReportUtils.ReportBean

/**
 *    author : shilu
 *    date   : 2024/8/19  15:16
 *    desc   :
 */

data class BlogMoreState(
    val notInterested: Boolean = false,
    val masking: Boolean = false,
    val report: Boolean = false,
    val reportList: SnapshotStateList<ReportBean> = mutableStateListOf(),
    val updateIndex: Int=0
)