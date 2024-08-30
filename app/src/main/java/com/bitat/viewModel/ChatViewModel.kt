package com.bitat.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.FindBaseByIdsDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            CuLog.debug(CuTag.SingleChat,"获取数据库版本$SingleRoomDB")
            SingleRoomDB.insertOrUpdate(
                selfId = UserStore.userInfo.id,
                otherId = otherInfo.id,
                unreads = 0
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
        getRooms()
    }

    fun chooseRoom(room: SingleRoomPo) {
        _state.update {
            it.copy(
                currentRoom = room,
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

        MainCo.launch {
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
                    it.chatList.addAll(tmpArr)
                    it
                }
            }
        }
    }

    fun clearRoom() {
        _state.update {
            it.copy(currentRoom = null, currentUserInfo = null)
        }
    }
}