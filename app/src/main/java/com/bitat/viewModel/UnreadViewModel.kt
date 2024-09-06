package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.FetchChatCommon
import com.bitat.repository.http.service.MsgReq
import com.bitat.repository.po.NoticePo
import com.bitat.repository.sqlDB.NoticeDB
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
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
                MsgReq.fetchChat(
                    FetchChatCommon().apply {
                        ack = false
                        fromId = 0
                        time = _state.value.lastMsgId
                        limit = fetchAmount.toLong()
                    }
                ).await().map { msgRes ->
                    msgRes.msgListList.forEach {
                        CuLog.info(
                            CuTag.SingleChat,
                            "get a message, data is $it"
                        )
                        SingleMsgDB.insertOne(
                            selfId = it.toId,
                            otherId = it.fromId,
                            time = it.time,
                            kind = it.kind.toShort(),
                            content = it.data.toStringUtf8(),
                            status = -1
                        )

                        SingleRoomDB.insertOrUpdate(
                            selfId = it.toId,
                            otherId = it.fromId,
                            unreads = 1
                        )

                        _state.update { kore ->
                            kore.copy(lastMsgId = it.time)
                        }
                    }

                    _state.update {
                        it.copy(unreadMsgCount = it.unreadMsgCount - fetchAmount)
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