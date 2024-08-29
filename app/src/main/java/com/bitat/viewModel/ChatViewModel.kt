package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> get() = _state.asStateFlow()

    fun createRoom(otherInfo: UserPartDto) {
        MainCo.launch {
            SingleRoomDB.insertOrUpdate(
                selfId = UserStore.userInfo.id,
                otherId = otherInfo.id,
                unreads = 0
            )
        }

        _state.update {
            it.copy(currentRoom = SingleRoomPo().apply {
                selfId = UserStore.userInfo.id
                otherId = otherInfo.id
                unreads = 0
            }, currentUserInfo = otherInfo)
        }
    }

    fun clearRoom() {
        _state.update {
            it.copy(currentRoom = null, currentUserInfo = null)
        }
    }
}