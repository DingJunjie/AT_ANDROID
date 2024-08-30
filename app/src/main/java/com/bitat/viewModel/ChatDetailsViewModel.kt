package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatDetailsState
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
class ChatDetailsViewModel : ViewModel() {
    val _state = MutableStateFlow(ChatDetailsState())
    val state: StateFlow<ChatDetailsState> get() = _state.asStateFlow()

    fun getMessage(toId: Long, pageSize: Int = 30, pageNo: Int = 0) {
        val msg = SingleMsgDB.findMsg(UserStore.userInfo.id, toId, pageNo, pageSize)
        _state.update {
            it.messageList.addAll(msg.first())
            it
        }
    }

    fun getNewMessage(newMsg: SingleMsgPo) {
        _state.update {
            it.messageList.add(newMsg)
            it
        }
    }

    fun sendMessage(toId: Long, kind: Int, content: String, completeFn: (SingleMsgPo) -> Unit) {
        val msg = SingleMsgPo()
        msg.kind = kind.toShort()
        msg.content = content
        msg.time = TimeUtils.getNow()
        msg.selfId = UserStore.userInfo.id
        msg.status = 1
        msg.otherId = toId
        SingleMsgDB.insertOne(msg.selfId, msg.otherId, msg.status, msg.time, msg.kind, msg.content)

        _state.update {
            it.messageList.add(0, msg)
            it
        }

        TcpClient.chat(toId, kind, content.toByteArray(charset("UTF-8")))
        completeFn(msg)
    }
}