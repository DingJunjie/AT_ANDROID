package com.bitat.repository.singleChat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.MainCo
import com.bitat.repository.dto.req.FindBaseByIdsDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.TcpHandler.newMsgFlow
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.store.UserStore
import com.bitat.viewModel.comparator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class GetRooms(val rooms: List<SingleRoomPo>)
class SetTop(val otherId: Long, val isTop: Int)
class SetMute(val otherId: Long, val isMute: Int)
class GetNewMessage(val msg: SingleMsgPo)

object SingleChatHelper {
    val singleChatUiFlow = MutableSharedFlow<Any>(extraBufferCapacity = 16)

    fun opsInit() {
        MainCo.launch(IO) {
            newMsgFlow.collect { it ->
                when (it) {
                    is MsgDto.ChatMsg -> {
                        handleChat(it)
                    }

                    is SetTopRoom -> {
                        it.cd.complete(
                            async {
                                SingleRoomDB.updateTop(
                                    it.isTop,
                                    UserStore.userInfo.id,
                                    it.otherId
                                )
                                return@async it.isTop
                            }.await().also { that ->
                                singleChatUiFlow.emit(
                                    SetTop(otherId = it.otherId, isTop = that)
                                )
                            }
                        )
                    }

                    is SetMuteRoom -> {
                        it.cd.complete(
                            async {
                                SingleRoomDB.updateMuted(
                                    it.isMute,
                                    UserStore.userInfo.id,
                                    it.otherId
                                )
                                return@async it.isMute
                            }.await().also { that ->
                                singleChatUiFlow.emit(
                                    SetMute(otherId = it.otherId, isMute = that)
                                )
                            }
                        )
                    }

                    is QueryChatRooms -> it.cd.complete(
                        async {
                            val list = getRooms()
                            return@async list
                        }.await().also { res ->
                            singleChatUiFlow.emit(GetRooms(rooms = res))
                        }
                    )
                }
            }
        }
    }

    suspend fun getRooms(): SnapshotStateList<SingleRoomPo> {
        val ids = arrayListOf<Long>()
        val arr = SingleRoomDB.getMagAndRoom(UserStore.userInfo.id)
        val tmpArr = mutableStateListOf<SingleRoomPo>()
        if (arr.isNotEmpty()) {
            tmpArr.addAll(arr)
        }

        tmpArr.forEach {
            ids.add(it.otherId)
        }

        val tmpMap = mutableMapOf<Long, UserBase1Dto>()

        UserReq.findBaseByIds(FindBaseByIdsDto(ids.toLongArray())).await()
            .map { res ->
                res.forEach {
                    tmpMap[it.id] = it
                }

                tmpArr.forEach { that ->
                    that.nickname = tmpMap[that.otherId]!!.nickname
                    that.profile = tmpMap[that.otherId]!!.profile
                    that.rel = tmpMap[that.otherId]!!.rel
                    that.revRel = tmpMap[that.otherId]!!.revRel
                    that.alias = tmpMap[that.otherId]!!.alias
                }

                tmpArr.sortWith(comparator)
            }

        return tmpArr
    }

    suspend fun setTop(otherId: Long, isTop: Int) = CompletableDeferred<Int>().also {
        newMsgFlow.emit(SetTopRoom(otherId, isTop, it))
    }

    suspend fun setMute(otherId: Long, isMute: Int) = CompletableDeferred<Int>().also {
        newMsgFlow.emit(SetMuteRoom(otherId, isMute, it))
    }

    suspend fun queryRooms() = CompletableDeferred<List<SingleRoomPo>>().also {
        newMsgFlow.emit(QueryChatRooms(it))
    }

    private suspend fun handleChat(msg: MsgDto.ChatMsg) {
        val newRoom = SingleRoomPo()
        newRoom.selfId = UserStore.userInfo.id
        newRoom.otherId = msg.fromId
        newRoom.unreads = 1
        SingleRoomDB.insertOrUpdate(newRoom)

        val room = SingleRoomDB.getRoom(UserStore.userInfo.id, msg.fromId)

        val content = msg.data.toString(Charsets.UTF_8)

        val nm = SingleMsgPo()
        nm.selfId = msg.toId
        nm.otherId = msg.fromId
        nm.status = -1
        nm.kind = msg.kind.toShort()
        nm.content = content
        nm.time = msg.time


        SingleMsgDB.insertOneUnique(nm)

        val rooms = getRooms()
        singleChatUiFlow.emit(GetRooms(rooms = rooms))
        singleChatUiFlow.emit(GetNewMessage(nm))
    }
}

class QueryChatRooms(val cd: CompletableDeferred<List<SingleRoomPo>>)
class SetTopRoom(val otherId: Long, val isTop: Int, val cd: CompletableDeferred<Int>)
class SetMuteRoom(val otherId: Long, val isMute: Int, val cd: CompletableDeferred<Int>)


