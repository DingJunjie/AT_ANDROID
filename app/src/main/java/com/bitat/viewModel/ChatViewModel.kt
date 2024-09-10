package com.bitat.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.SingleChatHelper
import com.bitat.repository.singleChat.TcpHandler
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatState
import com.bitat.utils.FileType
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.UPLOAD_OPS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val comparator = Comparator<SingleRoomPo> { l, r ->
    if (r.top == l.top) {
        (r.time - l.time).toInt()
    } else r.top - l.top
}

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> get() = _state.asStateFlow()

    fun updateRoomInfo(msg: SingleMsgPo) {
        _state.update {
            val index = it.chatList.indexOfFirst { that ->
                that.otherId == msg.otherId
            }
            it.chatList[index].content = msg.content
            it.chatList[index].time = msg.time
            it.chatList[index].kind = msg.kind

            it
        }
    }

    fun muteRoom(otherId: Long, isMuted: Boolean, completeFn: () -> Unit = {}) {
        _state.update {
            it.currentRoom.muted = if (isMuted) 1 else 0
            it
        }

        val i = _state.value.chatList.indexOfFirst {
            it.otherId == otherId
        }

        _state.value.chatList[i].muted = if (isMuted) 1 else 0

        SingleRoomDB.updateMuted(
            muted = if (isMuted) 1 else 0,
            UserStore.userInfo.id,
            otherId
        )
    }

    fun clearAllMessage(otherId: Long) {
        MainCo.launch(IO) {
            SingleMsgDB.clear(UserStore.userInfo.id, otherId)
        }
        _state.update { that ->
            that.chatList.map {
                if (it.otherId == otherId) {
                    it.content = ""
                }
                it
            }
            that
        }
    }

    fun setTop(otherId: Long, isTop: Boolean, completeFn: () -> Unit = {}) {
        MainCo.launch(IO) {
            _state.update { s ->
                val i = s.chatList.indexOfFirst { it.otherId == otherId }
                s.chatList[i].top = if (isTop) 1 else 0
                s.chatList.sortWith(comparator)

                if (s.currentRoom.id > 0) {
                    val updatedRoom = s.currentRoom.also {
                        it.top = if (isTop) 1 else 0
                    }

                    s.copy(currentRoom = updatedRoom, flag = s.flag+1)
                } else {
                    s.copy(flag = s.flag+1)
                }
            }
        }
    }

    fun changeBg(background: Uri, completeFn: () -> Unit) {

        MainCo.launch(IO) {
            QiNiuUtil.uploadSingleFile(
                background, UPLOAD_OPS.Chat, fileType = FileType.Image, true
            ) { key ->
                _state.update {
                    it.currentRoom.background = key
                    it
                }

                val i = _state.value.chatList.indexOfFirst {
                    it.otherId == _state.value.currentRoom.otherId
                }
                if (i >= 0) {
                    _state.value.chatList[i].background = key

                    SingleRoomDB.updateBg(
                        key,
                        _state.value.currentRoom.selfId,
                        _state.value.currentRoom.otherId
                    )
                }

                completeFn()
            }
        }
    }

    fun createRoom(otherInfo: UserPartDto) {
        MainCo.launch {
            CuLog.debug(CuTag.SingleChat, "获取数据库版本$SingleRoomDB")
            val u = SingleRoomPo()
            u.selfId = UserStore.userInfo.id
            u.otherId = otherInfo.id
            u.unreads = 0
            SingleRoomDB.insertOrUpdate(
                u
            )

            var r = SingleRoomDB.getRoom(selfId = UserStore.userInfo.id, otherId = otherInfo.id)
            if (r == null) {
                r = SingleRoomPo().apply {
                    selfId = UserStore.userInfo.id
                    otherId = otherInfo.id
                    unreads = 0
                    profile = otherInfo.profile
                    alias = otherInfo.alias
                    rel = otherInfo.rel
                    revRel = otherInfo.revRel
                    nickname = otherInfo.nickname
                }
            } else {
                r = r.apply {
                    nickname = otherInfo.nickname
                    profile = otherInfo.profile
                    rel = otherInfo.rel
                    revRel = otherInfo.revRel
                    alias = otherInfo.alias
                }
            }

            _state.update {
                it.chatList.removeIf { that ->
                    that.otherId == otherInfo.id
                }
                it.chatList.add(r)
                it.currentRoom = r

                it.chatList.sortWith(comparator)

                it.copy(
                    currentUserInfo = otherInfo
                )
            }
        }
    }

    init {
        MainCo.launch {
            delay(300L)
            SingleChatHelper.queryRooms()
        }
    }

    fun chooseRoom(room: SingleRoomPo) {
        _state.update {
            it.currentRoom.apply {
                id = room.id
                otherId = room.otherId
                selfId = room.selfId
                background = room.background
                cfg = room.cfg
                content = room.content
                time = room.time
                status = room.status
                kind = room.kind
                unreads = room.unreads
                profile = room.profile
                top = room.top
                muted = room.muted
                nickname = room.nickname
                alias = room.alias
            }
            it
        }

        MainCo.launch(IO) {
            SingleRoomDB.clearUnread(selfId = room.selfId, otherId = room.otherId)
        }
    }

    fun updateRooms(lists: List<SingleRoomPo>) {
        _state.update {
            it.chatList.clear()
            it.chatList.addAll(lists)
            it
        }
    }

    fun deleteRoom(otherId: Long) {
        SingleRoomDB.delete(selfId = UserStore.userInfo.id, otherId)

        _state.update {
            it.chatList.removeIf { that -> that.otherId == otherId }
            it
        }
    }

    fun clearRoom() {
        _state.update {
            it.currentRoom.apply {

            }
            it.copy(currentUserInfo = null)
        }
    }
}