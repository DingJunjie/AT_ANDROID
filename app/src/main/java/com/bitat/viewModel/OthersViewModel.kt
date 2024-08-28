package com.bitat.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.req.GetUserInfoDto
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.http.service.UserReq
import com.bitat.state.OthersState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OthersViewModel : ViewModel() {
    private val _othersState = MutableStateFlow(OthersState())
    val othersState: StateFlow<OthersState> get() = _othersState.asStateFlow()

    fun getUserInfo() {
        MainCo.launch {
            UserReq.userInfo(UserInfoDto(userId = othersState.value.userId.toLong())).await()
                .map { res ->
                    _othersState.update {
                        it.copy(userInfo = mutableStateOf(res))
                    }
                }
        }
    }

    fun switchTabbar(isTop: Boolean) {
        _othersState.update {
            it.copy(isTabbarTop = isTop)
        }
    }

    fun initUserId(userId: Int) {
        _othersState.update {
            it.copy(userId = userId)
        }

        getUserInfo()
    }
}