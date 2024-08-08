package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.repository.socket.TcpClient
import com.bitat.state.ChatDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
class ChatDetailsViewModel : ViewModel() {
    val state = MutableStateFlow(ChatDetailsState())

    fun onChatChang(msg: String) {
        state.update {
            it.copy(chatMsg = msg)
        }
    }

    fun sendClick(msg: String) {
        TcpClient.chat(125, 1, msg.toByteArray(charset("UTF-8")))
    }

}