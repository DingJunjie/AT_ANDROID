package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.consts.HTTP_DEFAULT
import com.bitat.repository.consts.HTTP_FAIL
import com.bitat.repository.consts.HTTP_SUCCESS
import com.bitat.repository.consts.REPORT_KIND_USER
import com.bitat.repository.dto.req.BlogOpsNotInterestedDto
import com.bitat.repository.dto.req.CreateUserReportDto
import com.bitat.repository.dto.req.DeleteBlogDto
import com.bitat.repository.dto.req.EditVisibleDto
import com.bitat.repository.dto.req.OpenAlbumDto
import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.http.service.AlbumReq
import com.bitat.repository.http.service.BlogOpsReq
import com.bitat.repository.http.service.BlogReq
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
                state.update { it.copy(masking = HTTP_SUCCESS) }
            }.errMap {
                state.update { it.copy(masking = HTTP_FAIL) }
                CuLog.error(CuTag.Blog, "masking fail====> code:${it.code},msg:${it.msg}")
            }
        }
    }

    // 不感兴趣
    fun notInterested(labels: IntArray) {
        MainCo.launch {
            BlogOpsReq.notInterested(BlogOpsNotInterestedDto(labels)).await().map {
                state.update { it.copy(notInterested = HTTP_SUCCESS) }
            }.errMap {
                state.update {
                    it.copy(notInterested = HTTP_FAIL)
                }
                CuLog.error(CuTag.Blog, "notInterested fail====> code:${it.code},msg:${it.msg}")
            }
        }
    }

    // 举报用户
    fun report(userId: Long) {
        MainCo.launch {
            val dto = CreateUserReportDto(REPORT_KIND_USER.toByte(), userId)
            val arrayList = ArrayList<Int>()
            state.value.reportList.filter { it.isSelect }.forEachIndexed { _, reportBean ->
                arrayList.add(reportBean.type)
            }
            dto.reason = arrayList.toIntArray()
            UserReportReq.createReport(dto).await().map {

                state.update { it.copy(report = HTTP_SUCCESS) }
                CuLog.debug(CuTag.Blog, "举报成功${state.value.report}")
            }.errMap {
                state.update { it.copy(report = HTTP_FAIL) }
                CuLog.error(CuTag.Blog, "report error,code:${it.code},msg:${it.msg}")
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

    fun isOther(other: Boolean) {
        state.update {
            it.copy(isOther = other)
        }
    }

    fun deleteBlog(blogId: Long, kind: Byte) {
        MainCo.launch {
            BlogReq.delete(DeleteBlogDto(blogId, kind)).await().map {
                state.update {
                    it.copy(deleteResp = HTTP_SUCCESS)
                }
            }.errMap {
                state.update {
                    it.copy(deleteResp = HTTP_FAIL)
                }
            }
        }
    }

    fun authBlog(blogId: Long, visible: Byte) {
        MainCo.launch {
            val dto = EditVisibleDto(blogId, visible)
            BlogReq.editVisible(dto).await().map {
                state.update {
                    it.copy(authResp = HTTP_SUCCESS)
                }
            }.errMap {
                state.update {
                    it.copy(authResp = HTTP_FAIL)
                }
            }
        }
    }

    fun authShow(isShow: Boolean) {
        state.update {
            it.copy(isAuthShow = isShow)
        }
    }

    fun dtAuthShow(isShow: Boolean) {
        state.update {
            it.copy(isDtAuthShow = isShow)
        }
    }

    fun dtAuthBlog(blogId: Long, albumOps: Long, cover: String) {
        MainCo.launch {
            val dto = OpenAlbumDto(blogId = blogId, albumOps = albumOps, cover = cover)
            AlbumReq.open(dto).await().map {
                state.update {
                    it.copy(dtAuthResp = HTTP_SUCCESS)
                }
            }.errMap {
                state.update {
                    it.copy(dtAuthResp = HTTP_FAIL)
                }
            }
        }
    }

    fun stateReset() {
        state.update {
            it.copy(authResp = HTTP_DEFAULT,
                deleteResp = HTTP_DEFAULT,
                dtAuthResp = HTTP_DEFAULT,
                notInterested = HTTP_DEFAULT,
                masking = HTTP_DEFAULT,
                report = HTTP_DEFAULT,
                isDtAuthShow = false,
                isAuthShow = false,
                isOther = false,
                updateIndex = 0)
        }
    }
}