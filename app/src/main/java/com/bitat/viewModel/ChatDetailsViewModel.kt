package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatDetailsState
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
class ChatDetailsViewModel : ViewModel() {
    val state = MutableStateFlow(ChatDetailsState())

    fun sendMessage(toId: Long, kind: Int, content: String) {
        val msg = SingleMsgPo()
        msg.kind = kind.toShort()
        msg.content = content
        msg.time = TimeUtils.getNow()
        msg.selfId = UserStore.userInfo.id
        msg.status = 0
        SingleMsgDB.insertOne(msg.selfId, msg.otherId, msg.status, msg.time, msg.kind, msg.content)

        state.update {
            it.messageList.add(msg)
            it
        }

        TcpClient.chat(toId, kind, content.toByteArray(charset("UTF-8")))
    }
}