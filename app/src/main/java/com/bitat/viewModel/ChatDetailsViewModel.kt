package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.singleChat.TcpClient
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

    fun sendClick(toId: Long, kind: Int, content: String) {
        TcpClient.chat(125, kind, content.toByteArray(charset("UTF-8")))
    }
}