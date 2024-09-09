package com.bitat.repository.singleChat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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

enum class ChatOps {
    GetRooms, SetTop, SetMute, GetMessage
}

data class SetTopParams(val otherId: Long, val isTop: Int)

object SingleChatHelper {
    val singleChatUiFlow = MutableSharedFlow<Map<ChatOps, Any>>(extraBufferCapacity = 16)

    fun singleMsgOpsInit() {
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
                                    mapOf(
                                        Pair(
                                            ChatOps.SetTop,
                                            SetTopParams(otherId = it.otherId, isTop = that)
                                        )
                                    )
                                )
                            }
                        )
                    }

                    is QueryChatRooms -> it.cd.complete(
                        async {
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
                            return@async tmpArr
                        }.await().also { res ->
                            singleChatUiFlow.emit(mapOf(Pair(ChatOps.GetRooms, res)))
                        }
                    )
                }
            }
        }
    }

    suspend fun setTop(otherId: Long, isTop: Int) = CompletableDeferred<Int>().also {
        newMsgFlow.emit(SetTopRoom(otherId, isTop, it))
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

        SingleMsgDB.insertOne(nm)

        singleChatUiFlow.emit(mapOf(Pair(ChatOps.GetRooms, room!!)))
        singleChatUiFlow.emit(mapOf(Pair(ChatOps.GetMessage, nm)))
    }
}

class QueryChatRooms(val cd: CompletableDeferred<List<SingleRoomPo>>)
class SetTopRoom(val otherId: Long, val isTop: Int, val cd: CompletableDeferred<Int>)


