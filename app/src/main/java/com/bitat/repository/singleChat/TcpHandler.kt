package com.bitat.repository.singleChat

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.sqlDB.SingleMsgDB
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object TcpHandler {
    val chatFlow = MutableSharedFlow<SingleMsgPo>()

    fun chat(msg: MsgDto.ChatMsg) {
        CuLog.debug(CuTag.SingleChat, "收到消息：${msg.data.toString(Charsets.UTF_8)}")
    }

    fun handleChat(msg: MsgDto.ChatMsg) {
        val newMsg = SingleMsgDB.insertOne(
            selfId = msg.toId,
            otherId = msg.fromId,
            status = -1,
            kind = msg.kind.toShort(),
            content = msg.data.toString(Charsets.UTF_8),
            time = msg.time
        )

        MainCo.launch {
            if (newMsg != null) {
                chatFlow.emit(newMsg)
            }
        }
    }
}