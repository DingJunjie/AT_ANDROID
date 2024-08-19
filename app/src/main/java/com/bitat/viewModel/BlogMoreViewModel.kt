package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.consts.REPORT_KIND_USER
import com.bitat.repository.dto.req.BlogOpsNotInterestedDto
import com.bitat.repository.dto.req.CreateUserReportDto
import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.http.service.BlogOpsReq
import com.bitat.repository.http.service.SocialReq
import com.bitat.repository.http.service.UserReportReq
import com.bitat.state.BlogMoreState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/8/19  15:14
 *    desc   :
 */

enum class BlogMore {
    NotInterested, Masking, Report, QrCode
}

class BlogMoreViewModel : ViewModel() {
    val state = MutableStateFlow(BlogMoreState())

    // 拉黑用户
    fun masking(userId: Long) {
        MainCo.launch {
            SocialReq.block(SocialDto(FOLLOWED, userId)).await().map {
                state.update { it.copy(masking = true) }
            }.errMap {
                state.update { it.copy(masking = false) } }
        }
    }

    // 不感兴趣
    fun notInterested(labels: IntArray) {
        MainCo.launch {
            BlogOpsReq.notInterested(BlogOpsNotInterestedDto(labels)).await().map {
                state.update { it.copy(notInterested = true) }
            }.errMap {
                state.update { it.copy(notInterested = false) }
            }
        }
    }

    // 举报用户
    fun report( sourceId: Long) {
        MainCo.launch {
            UserReportReq.createReport(CreateUserReportDto(REPORT_KIND_USER.toByte(), sourceId, arrayOf(""))).await()
                .map { }.errMap {

            }
        }
    }


}