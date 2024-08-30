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
import com.bitat.repository.http.service.BlogOpsReq
import kotlinx.coroutines.flow.update
import com.bitat.repository.dto.req.UpdateNicknameDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import com.bitat.repository.http.service.SearchReq
import com.bitat.repository.http.service.UserExtraReq
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.store.UserStore
import com.bitat.state.GENDER
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

    fun getMyWorks(userId:Long,lastTime: Long = 0, pageSize: Int = 20) {
        MainCo.launch {
            UserReq.photoBlogList(
                PhotoBlogListDto(
                    userId = userId,
                    pageSize = pageSize,
                    lastTime = lastTime
                )
            ).await().map { res ->
                CuLog.info(CuTag.Profile, "get my works, size is ${res.size}")
                uiState.update {
                    if (lastTime == 0L) {
                        it.myWorks.clear()
                    }
                    it.myWorks.addAll(res)
                    it
                }
            }.errMap {
                CuLog.error(CuTag.Profile, "has not get my works, ${it.msg}")
            }
        }
    }

    fun getMyFans(lastId: Long = 0, pageSize: Int = 20) {
        MainCo.launch {
            UserReq.findFansList(FindPrivateDto(lastTime = lastId, pageSize = pageSize)).await()
                .map { res ->
                    uiState.update {
                        if (lastId == 0L) {
                            it.fansList.clear()
                        }
                        it.fansList.addAll(res)
                        it
                    }
                }
        }
    }

    fun getMyFollows(lastId: Long = 0, pageSize: Int = 20) {
        MainCo.launch {
            UserReq.findFollowList(FindPrivateDto(lastTime = lastId, pageSize = pageSize)).await()
                .map { res ->
                    uiState.update {
                        if (lastId == 0L) {
                            it.followsList.clear()
                        }
                        it.followsList.addAll(res)
                        it
                    }
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
                if (res.isEmpty()) {
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
    fun lastIndex(index: Int) {
        uiState.update {
            it.copy(lastIndex = index)
        }
    }

    fun undateUser(){
//        uiState.update {
//
//        }
    }
}