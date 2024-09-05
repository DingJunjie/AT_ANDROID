package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.state.BrowserHistoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun worksUser() {

    }

    fun setCurrentTabIndex(index: Int) {
        _state.update {
            it.copy(currentTabIndex = index)
        }
    }

}