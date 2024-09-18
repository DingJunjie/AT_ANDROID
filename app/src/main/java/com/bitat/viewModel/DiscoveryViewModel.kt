package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.RecommendSearchDto
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.http.service.SearchReq
import com.bitat.repository.po.WORK_KIND
import com.bitat.repository.po.WatchHistoryPo
import com.bitat.repository.sqlDB.WatchHistoryDB
import com.bitat.repository.store.UserStore
import com.bitat.state.DiscoveryMenuOptions
import com.bitat.state.DiscoveryState
import com.bitat.utils.EmptyArray.int
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class DiscoveryViewModel : ViewModel() {
    private val _discoveryState = MutableStateFlow(DiscoveryState())
    val discoveryState: StateFlow<DiscoveryState> get() = _discoveryState.asStateFlow()

    fun switchMenu(menu: DiscoveryMenuOptions) {
        _discoveryState.update {
            it.copy(currentMenu = menu)
        }
    }

    fun firstFetchFinish() {
        _discoveryState.update {
            it.copy(isFirst = false)
        }
    }

    fun updateCollectStatus(item: BlogBaseDto, hasCollect: Boolean) {
        _discoveryState.update { it ->
            val i = it.discoveryList.indexOfFirst {
                that ->
                that == item
            }
            it.discoveryList[i].hasCollect = hasCollect
            it
        }
    }

    fun getDiscoveryList(isRefresh: Boolean = true, pageSize: Int = 30) {
        MainCo.launch {
            SearchReq.recommendSearch(RecommendSearchDto(pageSize, labels = IntArray(0)))
                .await().map { res ->
                    _discoveryState.update {
                        it.apply {
                            if (isRefresh) {
                                it.discoveryList.clear()
                            }
                            res.map { item ->
                                val random = Random.nextInt(1, 3)
                                item.height = random
                            }
                            it.discoveryList.addAll(res)
                        }
                    }
                }.errMap {
                    CuLog.error(
                        CuTag.Discovery,
                        "SearchReq recommendSearch error code:${it.code},msg:${it.msg} "
                    )
                }
        }
    }

    fun addHistory(blogBaseDto: BlogBaseDto) {

        viewModelScope.launch(Dispatchers.IO) {
            WatchHistoryDB.insertOne(WatchHistoryPo().apply {
                userId = UserStore.userInfo.id
                dataId = blogBaseDto.id
                kind = WORK_KIND
                time = TimeUtils.getNow()
            })
        }
    }

}