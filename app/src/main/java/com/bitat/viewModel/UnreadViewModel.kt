package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.req.FetchChatCommon
import com.bitat.repository.http.service.MsgReq
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.state.UnreadState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UnreadViewModel : ViewModel() {
    private val _state = MutableStateFlow(UnreadState())
    val state: StateFlow<UnreadState> get() = _state.asStateFlow()

    fun checkUnreadMessage() {
        MainCo.launch(IO) {
            MsgReq.count().await().map { that ->
                _state.update {
                    it.copy(unreadNoticeCount = that.notice, unreadMsgCount = that.chat)
                }


                if (_state.value.unreadMsgCount > 0) {
                    getUnreadMessage()
                }
            }
        }
    }

    fun getUnreadMessage() {
        if (state.value.unreadMsgCount > 0) {
            val fetchAmount =
                if (state.value.unreadMsgCount > 30) 30 else state.value.unreadMsgCount
            MainCo.launch(IO) {
                MsgReq.fetchChat(FetchChatCommon().apply {
                    ack = false
                    fromId = 0
                    time = _state.value.lastMsgId
                    limit = fetchAmount.toLong()
                }).await().map { msgRes ->
                    val msgPoArr = msgRes.msgListList.map {
                        SingleMsgPo().also { po ->
                            po.selfId = it.toId
                            po.otherId = it.fromId
                            po.time = it.time
                            po.kind = it.kind.toShort()
                            po.content = it.data.toStringUtf8()
                            po.status = -1
                        }
                    }.toTypedArray()

                    val unreadCountGroup = msgPoArr.groupBy {
                        it.otherId
                    }

                    unreadCountGroup.forEach {
                        val u = SingleRoomPo().apply {
                            this.selfId = UserStore.userInfo.id
                            this.otherId = it.key
                            this.unreads = it.value.size
                        }

                        SingleRoomDB.insertOrUpdate(
                            u
                        )
                    }
//                    SingleMsgDB.insertBatch(msgPoArr)

                    _state.update { kore ->
                        kore.copy(
                            lastMsgId = msgRes.msgListList.first().time,
                            unreadMsgCount = kore.unreadMsgCount - fetchAmount
                        )
                    }


                    getUnreadMessage()
                }
            }
        } else {
            return
        }
    }

    fun getUnreadNotice() {
        if (state.value.unreadNoticeCount > 0) {
            val fetchAmount =
                if (state.value.unreadNoticeCount > 30) 30 else state.value.unreadNoticeCount
            MainCo.launch(IO) {
                MsgReq.fetchNotice(FetchChatCommon().apply {
                    ack = false
                    time = _state.value.lastNoticeId
                    limit = fetchAmount.toLong()
                    fromId = 0
                }).await().map {
                    it.msgListList.forEach { that ->

                    }
                }
            }
        }
    }
}