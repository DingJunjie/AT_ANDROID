package com.bitat.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.FindBaseByIdsDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.po.RoomCfg
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatState
import com.bitat.utils.FileType
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.UPLOAD_OPS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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

    fun muteRoom(otherId: Long, isMuted: Boolean) {
        if (_state.value.currentRoom != null) {
            _state.update {
                it.copy(currentCfg = it.currentCfg.copy(muted = isMuted))
            }
        }
        val i = _state.value.chatList.indexOfFirst {
            it.otherId == otherId
        }
        val oldCfg = Json.decodeFromString(RoomCfg.serializer(), _state.value.chatList[i].cfg)
        oldCfg.muted = isMuted

        SingleRoomDB.updateCfg(
            Json.encodeToString(RoomCfg.serializer(), oldCfg),
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

    fun setTop(otherId: Long, isTop: Boolean) {
        MainCo.launch(IO) {
            SingleRoomDB.updateTop(
                if (isTop) 1 else 0,
                UserStore.userInfo.id,
                otherId
            )

            if (_state.value.currentRoom != null) {
                _state.update {
                    it.copy(currentCfg = it.currentCfg.copy(isTop = isTop))
                }

                _state.update { s ->
                    val i = s.chatList.indexOfFirst { it.otherId == otherId }
                    s.chatList[i].top = if (isTop) 1 else 0

                    val updatedRoom = s.currentRoom.also {
                        it!!.top = if (isTop) 1 else 0
                    }
                    s.copy(currentRoom = updatedRoom)
                }
            }


        }


    }

    fun changeBg(background: Uri) {

        MainCo.launch(IO) {
            QiNiuUtil.uploadSingleFile(
                background, UPLOAD_OPS.Pub, fileType = FileType.Image, true
            ) { key ->
                _state.update {
                    it.copy(currentCfg = it.currentCfg.copy(background = key))
                }

                val latestCfg = Json.encodeToString(RoomCfg.serializer(), _state.value.currentCfg)

                val i = _state.value.chatList.indexOf(_state.value.currentRoom)
                if (i > 0) {
                    _state.value.chatList[i].cfg = latestCfg

                    SingleRoomDB.updateCfg(
                        latestCfg,
                        _state.value.currentRoom!!.selfId,
                        _state.value.currentRoom!!.otherId
                    )
                }
            }
        }
    }

    fun sortMessage() {
        val comparator = Comparator<SingleRoomPo> { r, l ->
            if (r.top == l.top) {
                (r.time - l.time).toInt()
            } else r.top - l.top
        }
        _state.update {
            it.chatList.sortWith(comparator)
            it
        }
    }

    fun createRoom(otherInfo: UserPartDto) {
        MainCo.launch {
            CuLog.debug(CuTag.SingleChat, "获取数据库版本$SingleRoomDB")
            SingleRoomDB.insertOrUpdate(
                selfId = UserStore.userInfo.id, otherId = otherInfo.id, unreads = 0
            )
        }


        _state.update {
            val room = SingleRoomPo().apply {
                selfId = UserStore.userInfo.id
                otherId = otherInfo.id
                unreads = 0
                profile = otherInfo.profile
                alias = otherInfo.alias
                rel = otherInfo.rel
                revRel = otherInfo.revRel
                nickname = otherInfo.nickname
            }
            it.chatList.add(room)
            it.copy(
                currentRoom = room, currentUserInfo = otherInfo
            )
        }
    }

    init {
        MainCo.launch {
            delay(300L)
            getRooms()
        }
    }

    fun switchBg(uri: Uri) {

    }

    fun chooseRoom(room: SingleRoomPo) {
        _state.update {
            val cfg: RoomCfg
            if (room.cfg.isEmpty()) {
                cfg = RoomCfg(isTop = room.top > 0)
            } else {
                cfg = Json.decodeFromString(
                    RoomCfg.serializer(),
                    room.cfg
                )
                cfg.isTop = room.top > 0
            }
            it.copy(
                currentRoom = room,
                currentCfg = cfg
            )
        }
    }

    fun getRooms() {
        val ids = arrayListOf<Long>()
        val r = SingleRoomDB.getMagAndRoom(UserStore.userInfo.id)
        val arr = if (r.isNotEmpty()) r.first() else arrayOf()
        val tmpArr = mutableStateListOf<SingleRoomPo>()
        if (arr.isNotEmpty()) {
//            _state.update {
//                it.chatList.addAll(arr)
//                it
//            }
            tmpArr.addAll(arr)
        }

//        _state.value.chatList.forEach {
//            ids.add(it.otherId)
//        }

        tmpArr.forEach {
            ids.add(it.otherId)
        }

        val tmpMap = mutableMapOf<Long, UserBase1Dto>()

        MainCo.launch(Dispatchers.Default) {
            UserReq.findBaseByIds(FindBaseByIdsDto(ids.toLongArray())).await().map { res ->
                res.forEach {
                    tmpMap[it.id] = it
                }

                _state.update {
                    tmpArr.forEach { that ->
                        that.nickname = tmpMap[that.otherId]!!.nickname
                        that.profile = tmpMap[that.otherId]!!.profile
                        that.rel = tmpMap[that.otherId]!!.rel
                        that.revRel = tmpMap[that.otherId]!!.revRel
                        that.alias = tmpMap[that.otherId]!!.alias
                    }
//                    it.chatList.map { that ->
//                        that.nickname = tmpMap[that.otherId]!!.nickname
//                        that.profile = tmpMap[that.otherId]!!.profile
//                        that.rel = tmpMap[that.otherId]!!.rel
//                        that.revRel = tmpMap[that.otherId]!!.revRel
//                        that.alias = tmpMap[that.otherId]!!.alias
//                    }

                    val comparator = Comparator<SingleRoomPo> { l, r ->
                        if (r.top == l.top) {
                            (r.time - l.time).toInt()
                        } else r.top - l.top
                    }
                    tmpArr.sortWith(comparator)


                    it.chatList.clear()
                    it.chatList.addAll(tmpArr)
                    it
                }
            }
        }
    }

//    fun updateRoomContent(otherId: Long) {
//        val room = SingleRoomDB.getRoom(selfId = UserStore.userInfo.id, otherId) ?: return
//        _state.update {
//            val i = it.chatList.indexOfFirst { that -> that.otherId == otherId }
//            it.chatList[i].content = room.content
//
//            it
//        }
//    }

    fun deleteRoom(otherId: Long) {
        SingleRoomDB.delete(selfId = UserStore.userInfo.id, otherId)

        _state.update {
            it.chatList.removeIf { that -> that.otherId == otherId }
            it
        }
    }

    fun clearRoom() {
        _state.update {
            it.copy(currentRoom = null, currentUserInfo = null)
        }
    }
}