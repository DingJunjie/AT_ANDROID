package com.bitat.repository.singleChat

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object TcpHandler {
    val chatFlow = MutableSharedFlow<SingleMsgPo>(extraBufferCapacity = 16)
    val roomFlow = MutableSharedFlow<SingleRoomPo>(extraBufferCapacity = 16)

    fun chat(msg: MsgDto.ChatMsg) {
        CuLog.debug(CuTag.SingleChat, "收到消息：${msg.data.toString(Charsets.UTF_8)}")
    }

    fun handleChat(msg: MsgDto.ChatMsg) {
        val newRoom = SingleRoomPo()
        newRoom.selfId = UserStore.userInfo.id
        newRoom.otherId = msg.fromId
        newRoom.unreads = 1
        SingleRoomDB.insertOrUpdate(newRoom)

        val room = SingleRoomDB.getRoom(UserStore.userInfo.id, msg.fromId) ?: return

        val content = msg.data.toString(Charsets.UTF_8)

        val nm = SingleMsgPo()
        nm.selfId = msg.toId
        nm.otherId = msg.fromId
        nm.status = -1
        nm.kind = msg.kind.toShort()
        nm.content = content
        nm.time = msg.time

        val newMsg = SingleMsgDB.insertOne(
            nm
        )

        MainCo.launch(IO) {
            roomFlow.emit(room)
            if (newMsg != null) {
                chatFlow.emit(nm)
            }
        }
    }

    fun handleNotice(msg: MsgDto.NoticeMsg) {
        CuLog.debug(CuTag.SingleChat, "收到通知：${msg.data.toString(Charsets.UTF_8)}")
    }
}