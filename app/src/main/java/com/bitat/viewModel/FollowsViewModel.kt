package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.req.FindPrivateDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.service.UserReq
import com.bitat.state.FollowsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/12  17:26
 *    desc   :
 */

class FollowsViewModel : ViewModel() {
    private val _state = MutableStateFlow(FollowsState())
    val state = _state.asStateFlow()

    fun getMyFollows(lastId: Long = 0, pageSize: Int = 20) {
        MainCo.launch {
            UserReq.findFollowList(FindPrivateDto(lastTime = lastId, pageSize = pageSize)).await()
                .map { res ->
                    _state.update {
                        if (lastId == 0L) {
                            it.followsList.clear()
                        }
                        it.followsList.addAll(res)
                        it
                    }

                    _state.update {
                        if (res.isNotEmpty()) {
                            it.copy(loadResp = HttpLoadState.Success)
                        } else {
                            it.copy(loadResp = HttpLoadState.NoData)
                        }
                    }

                }.errMap {
                    CuLog.error(CuTag.Profile,
                        "UserReq findFollowList error code:${it.code},msg:${it.msg}")
                    _state.update {
                        it.copy(loadResp = HttpLoadState.Fail)
                    }
                }
        }
    }

    fun updateCurrentFollow(index: Int, item: UserBase1Dto) {
        _state.update {
            it.followsList.set(index, item)
            it
        }
        _state.update {
            it.copy(flag = it.flag + 1)
        }
    }

    fun isLoadMore(b: Boolean) {
        _state.update {
            it.copy(isLoadMore = b)
        }
    }

    fun removeFollow(index: Int) {
        _state.update {
            it.followsList.removeAt(index)
            it
        }
        _state.update {
            it.copy(flag = it.flag + 1)
        }
    }

}