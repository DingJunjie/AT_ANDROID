package com.bitat.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.dto.resp.UserHomeDto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.po.USER_KIND
import com.bitat.repository.po.WatchHistoryPo
import com.bitat.repository.sqlDB.WatchHistoryDB
import com.bitat.repository.store.UserStore
import com.bitat.state.OthersState
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OthersViewModel : ViewModel() {
    private val _othersState = MutableStateFlow(OthersState())
    val othersState: StateFlow<OthersState> get() = _othersState.asStateFlow()

    fun getUserInfo(success: (UserHomeDto) -> Unit = {}) {
        MainCo.launch {
            UserReq.userInfo(UserInfoDto(userId = othersState.value.userId)).await().map { res ->
                    _othersState.update {
                        it.copy(userInfo = mutableStateOf(res))
                    }
                    success(res)
                }.errMap {
                    CuLog.error(CuTag.Profile,
                        " UserReq  userInfo error code:${it.code},msg:${it.msg}")
                }
        }
    }

    fun switchTabbar(isTop: Boolean) {
        _othersState.update {
            it.copy(isTabBarTop = isTop)
        }
    }

    fun initUserId(userId: Long) {
        _othersState.update {
            it.copy(userId = userId)
        }
        getUserInfo()
    }

    fun tabType(index: Int) {
        _othersState.update {
            it.copy(profileType = index)
        }
    }

    fun atBottom(isBottom: Boolean) {
        _othersState.update {
            it.copy(isAtBottom = isBottom)
        }
    }

    fun addWatchHistory(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            WatchHistoryDB.insertOne(WatchHistoryPo().apply {
                userId = UserStore.userInfo.id
                kind = USER_KIND
                dataId = id
                time = TimeUtils.getNow()
            })
        }
    }


}