package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.sqlDB.NoticeMsgDB
import com.bitat.repository.store.UserStore
import com.bitat.state.NotificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> get() = _state.asStateFlow()

    fun getNotifications() {
        MainCo.launch {
            val res = NoticeMsgDB.find(UserStore.userInfo.id)
            _state.update {
                it.notifications.clear()
                it.notifications.addAll(res)
                it
            }
        }
    }

    fun getContent(kind: Long, content: String) {
//        when (kind) {
//
//        }
    }

    init {
        getNotifications()
    }
}