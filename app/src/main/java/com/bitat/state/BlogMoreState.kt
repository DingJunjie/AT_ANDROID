package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.repository.consts.HTTP_DEFAULT
import com.bitat.utils.ReportUtils.ReportBean

/**
 *    author : shilu
 *    date   : 2024/8/19  15:16
 *    desc   :
 */

data class BlogMoreState(
    val notInterested: Int =  HTTP_DEFAULT,
    val masking: Int = HTTP_DEFAULT,
    val report: Int =  HTTP_DEFAULT, // 默认-1 1 成功 2 失败
    val deleteResp:Int =HTTP_DEFAULT,
    val authResp:Int =HTTP_DEFAULT,
    val albumResp:Int=HTTP_DEFAULT,
    val dtAuthResp:Int =HTTP_DEFAULT,
    val isAuthShow:Boolean =false,
    val isDtAuthShow:Boolean =false,
    val reportList: SnapshotStateList<ReportBean> = mutableStateListOf(),
    val updateIndex: Int = 0,
    val isOther:Boolean= false)
