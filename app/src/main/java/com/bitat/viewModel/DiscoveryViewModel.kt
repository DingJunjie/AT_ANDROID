package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.req.RecommendSearchDto
import com.bitat.repository.http.service.SearchReq
import com.bitat.state.DiscoveryMenuOptions
import com.bitat.state.DiscoveryState
import com.bitat.utils.EmptyArray.int
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
                }
        }
    }
}