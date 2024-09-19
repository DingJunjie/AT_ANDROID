package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.FetchChatCommon
import com.bitat.repository.http.service.MsgReq
import com.bitat.repository.po.NoticeMsgPo
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.SingleMsgHelper
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.state.UnreadState
import com.bitat.utils.IntBox
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


                if (_state.value.unreadNoticeCount > 0) {
                    getUnreadNotice()
                }
            }
        }
    }

    private fun getUnreadMessage() {
        if (state.value.unreadMsgCount > 0) {
            MainCo.launch(IO) {
                MsgReq.fetchChat(FetchChatCommon().apply {
                    ack = true
                    fromId = _state.value.lastMsgId
                    time = _state.value.lastTime
                    limit = 50
                }).await().map { msgRes ->
                    CuLog.info(
                        CuTag.SingleChat,
                        "获取到新的未读消息$msgRes, 有${msgRes.msgListList.size}条"
                    )
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

                    val unreadCountGroup = HashMap<Long, IntBox>()
                    for (po in msgPoArr) {
                        val count = unreadCountGroup[po.otherId]
                        if (count == null) {
                            unreadCountGroup[po.otherId] = IntBox(1)
                        } else count.v += 1
                    }

                    /**
                     * val unreadCountGroup = msgPoArr.groupBy {
                     *      it.otherId
                     * }
                     */

                    unreadCountGroup.forEach {
                        val room = SingleRoomPo().apply {
                            this.selfId = UserStore.userInfo.id
                            this.otherId = it.key
                            this.unreads = it.value.v
                        }

                        SingleRoomDB.insertOrUpdate(
                            room
                        )
                    }

                    val filteredList =
                        SingleMsgDB.filterDuplicate(UserStore.userInfo.id, -1, msgPoArr)
                    SingleMsgDB.insertArray(filteredList)

                    _state.update { that ->
                        that.copy(
                            lastMsgId = msgRes.msgListList.last().fromId,
                            lastTime = msgRes.msgListList.last().time,
                            unreadMsgCount = that.unreadMsgCount - msgPoArr.size
                        )
                    }

                    getUnreadMessage()
                }
            }
        } else {
            MainCo.launch {
                MsgReq.fetchChat(FetchChatCommon().apply {
                    ack = true
                    fromId = _state.value.lastMsgId
                    time = _state.value.lastTime
                    limit = 50
                }).await().map {
                    _state.update {
                        it.copy(unreadMsgCount = 0, lastTime = 0, lastMsgId = 0)
                    }
                }
            }
            return
        }
    }

    private fun getUnreadNotice() {
        if (state.value.unreadNoticeCount > 0) {
            MainCo.launch(IO) {
                MsgReq.fetchNotice(FetchChatCommon().apply {
                    ack = true
                    time = _state.value.lastNoticeTime
                    limit = 50
                    fromId = _state.value.lastNoticeId
                }).await().map {
                    it.msgListList.map { that ->
                        SingleMsgHelper.handleNotice(that)
                    }

                    if (it.msgListList.isEmpty()) {
                        return@launch
                    }

                    _state.update { kore ->
                        kore.copy(
                            unreadNoticeCount = kore.unreadMsgCount - it.msgListList.size,
                            lastNoticeTime = it.msgListList.last().time,
                            lastNoticeId = it.msgListList.last().fromId
                        )
                    }
                }
            }

            getUnreadMessage()
        } else {
            MainCo.launch {
                MsgReq.fetchNotice(FetchChatCommon().apply {
                    ack = false
                    time = _state.value.lastNoticeTime
                    limit = 1
                    fromId = _state.value.lastNoticeId
                }).await().map {
                    _state.update {
                        it.copy(unreadMsgCount = 0, lastNoticeTime = 0, lastNoticeId = 0)
                    }
                }
            }
            return

        }
    }
}