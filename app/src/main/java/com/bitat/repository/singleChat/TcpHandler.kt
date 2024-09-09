package com.bitat.repository.singleChat

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object TcpHandler {

    val newMsgFlow = MutableSharedFlow<Any>(extraBufferCapacity = 16)

    fun chat(msg: MsgDto.ChatMsg) {
        CuLog.debug(CuTag.SingleChat, "收到消息：${msg.data.toString(Charsets.UTF_8)}")
    }

    fun handleChat(msg: MsgDto.ChatMsg) {
        MainCo.launch(IO) {
            newMsgFlow.emit(msg)
        }
    }

    fun handleNotice(msg: MsgDto.NoticeMsg) {
        CuLog.debug(CuTag.SingleChat, "收到通知：${msg.data.toString(Charsets.UTF_8)}")
    }
}