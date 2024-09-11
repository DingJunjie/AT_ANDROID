package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.HTTP_PAGESIZE
import com.bitat.repository.dto.req.FindPrivateDto
import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.service.SocialReq
import com.bitat.repository.http.service.UserReq
import com.bitat.state.BlackListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlackListViewModel : ViewModel() {
    private val _state = MutableStateFlow(BlackListState())
    val state: StateFlow<BlackListState> = _state.asStateFlow()

    fun blackList(isInit: Boolean = true) {
        MainCo.launch {
            UserReq.findBlackList(FindPrivateDto(HTTP_PAGESIZE)).await().map { result ->
                _state.update {
                    if (isInit) it.myBlackList.clear()
                    it.myBlackList.addAll(result)
                    it
                }
            }.errMap {
                CuLog.error(CuTag.Profile,
                    "UserReq  findBlackList error code:${it.code},msg:${it.msg}")
            }

        }
    }

    fun removeBlackList(item: UserBase1Dto, onComplete: (Boolean) -> Unit) {
        MainCo.launch {
            SocialReq.follow(SocialDto(0, item.id)).await().map {
                onComplete(true)
            }.errMap {
                CuLog.error(CuTag.Profile, "SocialReq  follow error code:${it.code},msg:${it.msg}")
                onComplete(true)
            }
        }
    }

    fun setCurrent(item: UserBase1Dto, complete: () -> Unit) {
        _state.update {
            it.currentUser = item
            it
        }

        _state.update {
            it.copy(flag = _state.value.flag + 1)
        }
        complete()

    }
}