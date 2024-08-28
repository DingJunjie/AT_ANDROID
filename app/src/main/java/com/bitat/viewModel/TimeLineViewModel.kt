package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.TimeLineDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.store.UserStore
import com.bitat.state.TimeLineState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/8/27  18:13
 *    desc   : 时间线
 */
class TimeLineViewModel : ViewModel() {

    val state = MutableStateFlow(TimeLineState())


    fun timeLineInit(lastTime: Long = 0L) {
        MainCo.launch {
            BlogReq.timeLine(TimeLineDto(20, UserStore.userInfo.id)).await().map { data ->
                if (lastTime > 0) state.update {
                    it.blogList.clear()
                    it
                }

                state.update {
                    it.blogList.addAll(data)
                    it
                }
            }.errMap {
                CuLog.error(CuTag.Profile, "timeLine fail code:${it.code},msg:${it.msg}")
            }
        }
    }
}