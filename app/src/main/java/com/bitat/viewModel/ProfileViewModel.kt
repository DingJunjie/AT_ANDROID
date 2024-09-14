package com.bitat.viewModel

import androidx.compose.material3.DrawerValue
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.HTTP_PAGESIZE
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.req.BlogOpsAgreeHistoryDto
import com.bitat.repository.dto.req.FindPrivateDto
import com.bitat.repository.dto.req.PhotoBlogListDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.service.BlogOpsReq
import kotlinx.coroutines.flow.update
import com.bitat.repository.dto.resp.UserDto
import com.bitat.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import com.bitat.repository.http.service.UserReq
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {
    val uiState = MutableStateFlow(ProfileUiState())

    fun closeDrawer() {
        uiState.update {
            it.copy(drawerStateValue = DrawerValue.Closed)
        }
    }

    fun updateTabIndex(index: Int) {
        uiState.update {
            it.copy(currentTabIndex = index)
        }
    }

    fun switchTabbar(isTop: Boolean) {
        uiState.update {
            it.copy(isTabbarTop = isTop)
        }
    }

    fun openDrawer() {
        uiState.update {
            it.copy(drawerStateValue = DrawerValue.Open)
        }
    }

    fun getMyWorks(userId: Long, lastTime: Long = 0, pageSize: Int = HTTP_PAGESIZE) {
        MainCo.launch {
            isFootShow(true)
            httpResp(true)
            UserReq.photoBlogList(PhotoBlogListDto(userId = userId,
                pageSize = pageSize,
                lastTime = lastTime)).await().map { res ->
                CuLog.info(CuTag.Profile, "get my works, size is ${res.size}")
                httpResp(false)
                uiState.update {
                    if (lastTime == 0L) {
                        it.myWorks.clear()
                    }
                    it.myWorks.addAll(res)
                    it
                }
                if (res.isEmpty()||res.size<HTTP_PAGESIZE) {
                    httpState(HttpLoadState.NoData)
                } else {
                    isFootShow(false)
                    httpState(HttpLoadState.Default)
                }
            }.errMap {
                httpResp(false)
                httpState(HttpLoadState.Fail)
                CuLog.error(CuTag.Profile, "has not get my works, ${it.msg}")
            }
        }
    }




    fun getMyPraise(lastTime: Long = 0, pageSize: Int = HTTP_PAGESIZE) {
        if (uiState.value.isReq) return
        MainCo.launch {
            isFootShow(true)
            httpResp(true)

            BlogOpsReq.agreeHistory(BlogOpsAgreeHistoryDto(pageSize, lastTime)).await().map { res ->
                CuLog.info(CuTag.Profile, "get my Praise, size is ${res.size}")
                httpResp(false)
                uiState.update {
                    if (lastTime == 0L) {
                        it.myPraise.clear()
                    }
                    it.myPraise.addAll(res)
                    it
                }
                if (res.isEmpty()||res.size<HTTP_PAGESIZE) {
                    httpState(HttpLoadState.NoData)
                } else {
                    isFootShow(false)
                    httpState(HttpLoadState.Default)
                }
            }.errMap {
                httpResp(false)
                httpState(HttpLoadState.Fail)
                CuLog.error(CuTag.Profile, "has not get my Praise, ${it.msg}")
            }
        }
    }

    fun atBottom(isBottom: Boolean) {
        uiState.update {
            CuLog.error(CuTag.Profile, "atBottom $isBottom")
            it.copy(isAtBottom = isBottom)
        }
    }

    fun tabType(index: Int) {
        uiState.update {
            it.copy(profileType = index)
        }
    }

    private fun httpResp(result: Boolean) {
        uiState.update {
            it.copy(isReq = result)
        }
    }

    private fun isFootShow(show: Boolean) {
        uiState.update {
            it.copy(isFootShow = show)
        }
    }

    private fun httpState(state: HttpLoadState) {
        uiState.update {
            it.copy(httpState = state)
        }
    }

    fun updateUser(newUser: UserDto) {
        uiState.update {
            it.user = newUser
            it
        }
        uiState.update { it.copy(updateFlag = uiState.value.updateFlag + 1) }
    }

    fun showSuccess(result: Boolean) {
        uiState.update { it.copy(showSuccess = result) }
    }

    fun showFail(result: Boolean) {
        uiState.update { it.copy(showFail = result) }
    }

    fun clearVm() {
        onCleared()
    }


}