package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.state.SerachTFState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 *    author : shilu
 *    date   : 2024/8/5  09:56
 *    desc   :
 */
class SearchTFViewModel : ViewModel() {
    val searchTFState = MutableStateFlow(SerachTFState())

    fun onTextChange(str: String) {
        searchTFState.update {
            it.copy(searchText = str) //
        }
        searchTFState.update {
            it.copy(isShowClose = str.isNotEmpty())
        }
    }

    fun closeOnClick() {
        searchTFState.update {
            it.copy(searchText = "")
        }
        searchTFState.update {
            it.copy(isShowClose = false)
        }
    }
}