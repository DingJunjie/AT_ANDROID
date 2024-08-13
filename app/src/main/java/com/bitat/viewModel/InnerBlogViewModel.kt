package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.state.InnerBoleState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 *    author : shilu
 *    date   : 2024/8/13  17:46
 *    desc   :
 */
class InnerBlogViewModel : ViewModel() {
    val innerState = MutableStateFlow(InnerBoleState())


    fun onPageChange(pageIndex: Int) {
        innerState.update {
            it.copy(currentIndex = pageIndex)
        }
    }
}