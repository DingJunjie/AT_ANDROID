package com.bitat.viewModel

import android.view.View
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.req.FindPrivateDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.service.UserReq
import com.bitat.state.FansState
import com.bitat.state.FollowsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/12  18:24
 *    desc   :
 */
class FansViewModel : ViewModel() {
    private val _state = MutableStateFlow(FansState())
    val state = _state.asStateFlow()

    fun getMyFans(lastId: Long = 0, pageSize: Int = 20) {
        MainCo.launch {
            UserReq.findFansList(FindPrivateDto(lastTime = lastId, pageSize = pageSize)).await()
                .map { res ->
                    _state.update {
                        if (lastId == 0L) {
                            it.fansList.clear()
                        }
                        it.fansList.addAll(res)
                        it
                    }
                }
        }
    }

    fun updateCurrentFans(index: Int, item: UserBase1Dto) {
        _state.update {
            it.fansList.set(index, item)
            it
        }

    }
}