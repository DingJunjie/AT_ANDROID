package com.bitat.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import com.bitat.state.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> get() = _homeState.asStateFlow()

    fun setIndex(newIndex: Int) {
        _homeState.update {
            it.copy(selectedIndex = newIndex)
        }
    }

    fun setBottom(height: Dp) {
        _homeState.update {
            it.copy(bottomSize = height)
        }
    }
}