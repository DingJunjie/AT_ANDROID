package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.QueryCoverDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.sqlDB.WatchHistoryDB
import com.bitat.repository.store.UserStore
import com.bitat.state.BrowserHistoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/5  10:06
 *    desc   :
 */
class BrowserHistoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(BrowserHistoryState())
    val state: StateFlow<BrowserHistoryState> get() = _state.asStateFlow()

    fun browserUser() {

    }
    fun worksHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = async { WatchHistoryDB.find(UserStore.userInfo.id) }.await()
            val idArr = arrayListOf<QueryCoverDto>()

            ids.forEachIndexed { index, watchHistoryPo ->
                val dto = QueryCoverDto(watchHistoryPo.dataId, watchHistoryPo.time)
                idArr.add(dto)
            }
            if (idArr.isNotEmpty()) {
                BlogReq.queryHistory(idArr.toTypedArray()).await().map { result ->
                    _state.update {
                        it.myWorks.addAll(result)
                        it
                    }
                }.errMap {
                    CuLog.error(CuTag.Profile,
                        "BlogReq queryHistory error code:${it.code},msg:${it.msg}")
                }

            }


        }
    }

    fun setCurrentTabIndex(index: Int) {
        _state.update {
            it.copy(currentTabIndex = index)
        }
    }


}