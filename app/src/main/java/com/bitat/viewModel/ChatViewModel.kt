package com.bitat.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.dto.resp.UserHomeDto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.SingleMsgHelper
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
            if (index > -1) {
                it.chatList[index].content = msg.content
                it.chatList[index].time = msg.time
                it.chatList[index].kind = msg.kind

                it.chatList.sortWith(comparator)
            } else {
                MainCo.launch {
                    UserReq.userInfo(UserInfoDto(userId = msg.otherId)).await().map { info ->
                        val room = SingleRoomPo().apply {
                            selfId = UserStore.userInfo.id
                            otherId = msg.otherId
                            kind = msg.kind
                            content = msg.content
                            time = msg.time
                            status = 1
                            unreads = 0
                            nickname = info.nickname
                            profile = info.profile
                            alias = info.alias
                            rel = info.rel
                            revRel = info.revRel
                        }
                        SingleRoomDB.insertOrUpdate(room)
                        val r = SingleRoomDB.getRoom(UserStore.userInfo.id, msg.otherId)
                        if (r == null) {
                            it.chatList.add(0, room)
                        } else {
                            r.apply {
                                nickname = info.nickname
                                profile = info.profile
                                alias = info.alias
                                rel = info.rel
                                revRel = info.revRel
                                kind = msg.kind
                                content = msg.content
                                time = msg.time
                            }
                            it.chatList.add(0, r)
                        }
                    }
                }
            }

            it
        }
    }

    fun muteRoom(otherId: Long, isMuted: Boolean, completeFn: () -> Unit = {}) {
        if (_state.value.currentRoom.id > 0) {
            _state.update {
                it.currentRoom.muted = if (isMuted) 1 else 0
                it
            }
        }

        _state.update {
            val i = it.chatList.indexOfFirst { that ->
                that.otherId == otherId
            }

            it.chatList[i].muted = if (isMuted) 1 else 0
            it.copy(flag = it.flag + 1)
        }

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

                    s.copy(currentRoom = updatedRoom, flag = s.flag + 1)
                } else {
                    s.copy(flag = s.flag + 1)
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
                    it.copy(flag = it.flag + 1)
                }

                val i = _state.value.chatList.indexOfFirst {
                    it.otherId == _state.value.currentRoom.otherId
                }
                if (i >= 0) {
                    _state.value.chatList[i].background = key

                    SingleRoomDB.updateBg(
                        key, _state.value.currentRoom.selfId, _state.value.currentRoom.otherId
                    )
                }

                completeFn()
            }
        }
    }

    fun createRoom(oId: Long, needUpdateCurrent: Boolean = false, completeFn: () -> Unit = {}) {
        // 不存在的话啊，数据库读，如果数据库没有就插入一条，有的话就读取插入
        MainCo.launch {
            CuLog.debug(CuTag.SingleChat, "获取数据库版本$SingleRoomDB")

            UserReq.userInfo(UserInfoDto(userId = oId)).await().map { otherInfo ->
                val u = SingleRoomPo()
                u.selfId = UserStore.userInfo.id
                u.otherId = otherInfo.id
                u.unreads = 0
                SingleRoomDB.insertOrUpdate(u)

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
                    it.chatList.add(0, r)
                    it.chatList.sortWith(comparator)

                    if (needUpdateCurrent) {
                        it.copy(
                            currentRoom = r,
                            currentUserInfo = otherInfo
                        )
                    } else it
                }

                completeFn()
            }
        }
    }

    fun createRoomByProfile(otherInfo: UserHomeDto) {
        val i = _state.value.chatList.indexOfFirst {
            it.otherId == otherInfo.id
        }

        if (i > -1) {
            // 如果存在
            _state.update {
                it.currentRoom = _state.value.chatList[i]
                it.chatList.sortWith(comparator)

                it.copy(
                    currentUserInfo = otherInfo
                )
            }
            return
        } else {
            val u = SingleRoomPo().apply {
                selfId = UserStore.userInfo.id
                otherId = otherInfo.id
                unreads = 0
                profile = otherInfo.profile
                nickname = otherInfo.nickname
                alias = otherInfo.alias
                rel = otherInfo.rel
                revRel = otherInfo.revRel
            }


            _state.update {
                it.copy(
                    currentUserInfo = otherInfo, currentRoom = u
                )
            }
        }


    }

    init {
        MainCo.launch {
            delay(300L)
            SingleMsgHelper.queryRooms()
        }
    }

    fun chooseRoom(room: SingleRoomPo) {
        _state.update {
            val i = it.chatList.indexOfFirst { that ->
                that.otherId == room.otherId
            }
            it.chatList[i].unreads = 0
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
                unreads = 0
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

    fun updateRoomContent(newMsg: SingleMsgPo) {
        MainCo.launch {
            val i = _state.value.chatList.indexOfFirst { that ->
                that.otherId == newMsg.otherId
            }

            if (i > -1) {
                _state.update {
                    it.chatList[i].apply {
                        content = newMsg.content
                        kind = newMsg.kind
                        time = newMsg.time
                        unreads += 1
                    }

                    it
                }
            } else {
                createRoom(newMsg.otherId, false) {}
            }

            _state.update {
                it.chatList.sortWith(comparator)
                it.copy(flag = it.flag + 1)
            }
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
            it.copy(currentUserInfo = null, currentRoom = SingleRoomPo())
        }
    }
}