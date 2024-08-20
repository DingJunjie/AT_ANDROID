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
import com.bitat.utils.ReportUtils.ReportBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val _state = MutableStateFlow(BlogMoreState()).asStateFlow()

    // 拉黑用户
    fun masking(userId: Long) {
        MainCo.launch {
            SocialReq.block(SocialDto(FOLLOWED, userId)).await().map {
                state.update { it.copy(masking = true) }
            }.errMap {
                state.update { it.copy(masking = false) }
            }
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
    fun report(userId: Long) {
        MainCo.launch {
            val dto = CreateUserReportDto(REPORT_KIND_USER.toByte(),userId)
            val arrayList = ArrayList<Int>()
            state.value.reportList.filter { it.isSelect }.forEachIndexed { index, reportBean ->
                arrayList.add(reportBean.type)
            }
            dto.reason = arrayList.toIntArray()
            UserReportReq.createReport(dto).await().map {
                it.toString()
                state.update { it.copy(report = true) }
            }.errMap {
                state.update { it.copy(report = false) }
            }
        }
    }

    fun setReportList(report: Array<ReportBean>) {
        state.update {
            it.reportList.clear()
            it.reportList.addAll(report)
            it
        }
    }

    fun selectRepor(report: ReportBean) {
        val index = state.value.reportList.indexOf(report)
        state.update {
            it.reportList[index] = report
            it

        }

        state.update {
            state.value.updateIndex + 1
            it.copy(updateIndex = state.value.updateIndex + 1)
        }
    }


}