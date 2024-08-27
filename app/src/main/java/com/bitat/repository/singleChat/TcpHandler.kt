package com.bitat.repository.singleChat

import com.bitat.log.CuLog
import com.bitat.log.CuTag

object TcpHandler {
    fun chat(msg: MsgDto.ChatMsg) {
        CuLog.debug(CuTag.SingleChat, "收到消息：${msg.data.toString(Charsets.UTF_8)}")


    }
}