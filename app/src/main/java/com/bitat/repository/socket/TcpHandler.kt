package com.bitat.repository.socket

import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.pbDto.MsgDto

object TcpHandler {
    fun chat(msg: MsgDto.ChatMsg) {
        CuLog.debug(CuTag.SingleChat, "收到消息：${msg.data.toString(Charsets.UTF_8)}")
    }
}