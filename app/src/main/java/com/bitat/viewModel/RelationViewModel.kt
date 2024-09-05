package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.state.RelationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RelationViewModel : ViewModel() {
    private val _state = MutableStateFlow(RelationState())
    val state: StateFlow<RelationState> get() = _state.asStateFlow()


}