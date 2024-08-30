package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.req.FindBaseByIdsDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.http.service.UserReq
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

    fun createRoom(otherInfo: UserPartDto) {
        MainCo.launch {
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

    fun getRooms() {
        val ids = arrayListOf<Long>()
        val r = SingleRoomDB.getMagAndRoom(UserStore.userInfo.id)
        val arr = if (r.isNotEmpty()) r.first() else arrayOf()
        if (arr.isNotEmpty()) {
            _state.update {
                it.chatList.addAll(arr)
                it
            }
        }

        _state.value.chatList.forEach {
            ids.add(it.otherId)
        }

        val tmpMap = mutableMapOf<Long, UserBase1Dto>()

        MainCo.launch {
            UserReq.findBaseByIds(FindBaseByIdsDto(ids.toLongArray())).await().map { res ->
                res.forEach {
                    tmpMap[it.id] = it
                }

                _state.update {
                    it.chatList.map { that ->
                        that.nickname = res[that.otherId.toInt()].nickname
                        that.profile = res[that.otherId.toInt()].profile
                        that.rel = res[that.otherId.toInt()].rel
                        that.revRel = res[that.otherId.toInt()].revRel
                        that.alias = res[that.otherId.toInt()].alias
                    }
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