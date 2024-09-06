package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.state.NotificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> get() = _state.asStateFlow()

    fun getNotification() {

    }
}