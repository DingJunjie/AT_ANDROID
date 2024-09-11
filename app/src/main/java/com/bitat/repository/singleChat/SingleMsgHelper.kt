package com.bitat.repository.singleChat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.*
import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.dto.req.FindBaseByIdsDto
import com.bitat.repository.dto.req.GetBlogDto
import com.bitat.repository.dto.req.NoticeReqDto
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserHomeDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.http.service.NoticeReq
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.po.NoticeMsgPo
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.po.SystemNoticeMsgPo
import com.bitat.repository.singleChat.TcpHandler.newMsgFlow
import com.bitat.repository.sqlDB.NoticeMsgDB
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.sqlDB.SystemNoticeMsg
import com.bitat.repository.store.UserStore
import com.bitat.viewModel.comparator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class GetRooms(val rooms: List<SingleRoomPo>)
class SetTop(val otherId: Long, val isTop: Int)
class SetMute(val otherId: Long, val isMute: Int)
class GetNewMessage(val msg: SingleMsgPo)

class GetNewNotice(val notice: NoticeMsgPo)

@Serializable
data class SystemNotice(
    val id: Long = 0,
    val text: String = ""
)

@Serializable
data class SocialNotice(
    val id: Long = 0,
    val blogId: Long = 0,
    val commentId: Long = 0,
    val comment: String = "",
    val image: String = "",
)

@Serializable
data class NoticeContent(
    val commentId: Long = 0,
    val comment: String = "",
)

object SingleMsgHelper {
    val singleChatUiFlow = MutableSharedFlow<Any>(extraBufferCapacity = 16)

    fun opsInit() {
        MainCo.launch(IO) {
            newMsgFlow.collect { it ->
                when (it) {
                    is MsgDto.ChatMsg -> {
                        handleChat(it)
                    }

                    is MsgDto.NoticeMsg -> {
                        handleNotice(it)
                    }

                    is SetTopRoom -> {
                        it.cd.complete(async {
                            SingleRoomDB.updateTop(
                                it.isTop, UserStore.userInfo.id, it.otherId
                            )
                            return@async it.isTop
                        }.await().also { that ->
                            singleChatUiFlow.emit(
                                SetTop(otherId = it.otherId, isTop = that)
                            )
                        })
                    }

                    is SetMuteRoom -> {
                        it.cd.complete(async {
                            SingleRoomDB.updateMuted(
                                it.isMute, UserStore.userInfo.id, it.otherId
                            )
                            return@async it.isMute
                        }.await().also { that ->
                            singleChatUiFlow.emit(
                                SetMute(otherId = it.otherId, isMute = that)
                            )
                        })
                    }

                    is QueryChatRooms -> it.cd.complete(async {
                        val list = getRooms()
                        return@async list
                    }.await().also { res ->
                        singleChatUiFlow.emit(GetRooms(rooms = res))
                    })
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

        UserReq.findBaseByIds(FindBaseByIdsDto(ids.toLongArray())).await().map { res ->
            res.forEach {
                tmpMap[it.id] = it
            }

            tmpArr.forEach { that ->
                if (tmpMap[that.otherId] != null) {
                    that.nickname = tmpMap[that.otherId]!!.nickname
                    that.profile = tmpMap[that.otherId]!!.profile
                    that.rel = tmpMap[that.otherId]!!.rel
                    that.revRel = tmpMap[that.otherId]!!.revRel
                    that.alias = tmpMap[that.otherId]!!.alias
                }
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

    /*
    *   const val APP_NOTICE_FOLLOW = 1            // 关注
        const val APP_NOTICE_NOT_FOLLOW = 2        // 取消关注
        const val APP_NOTICE_BLOCK = 3             // 拉黑
        const val APP_NOTICE_NOT_BLOCK = 4         // 取消拉黑
        const val APP_NOTICE_AT = 5                // at
        const val APP_NOTICE_AGREE_BLOG = 6        // 博文点赞
        const val APP_NOTICE_AGREE_COMMENT = 7     // 一级评论点赞
        const val APP_NOTICE_AGREE_SUB_COMMENT = 8 // 二级评论点赞
        const val APP_NOTICE_REPLY_COMMENT = 9     // 创建一级评论
        const val APP_NOTICE_REPLY_SUB_COMMENT = 10// 创建评论
        const val APP_NOTICE_BLOG_CO_CREATE = 11   // 用户共创
        const val APP_NOTICE_REAL = 12             // 用户实名认证
        const val APP_NOTICE_ALBUM = 13            // 跟动态
        const val APP_NOTICE_USER_INFO = 14        // 谁访问了我
* */

    private suspend fun handleNotice(msg: MsgDto.NoticeMsg) {
        val originContent = msg.data.toString(Charsets.UTF_8)
        val newNotice = NoticeMsgPo().apply {
            fromId = msg.fromId
            userId = msg.toId
            content = ""
            time = msg.time
            kind = msg.kind
        }

        CuLog.info(CuTag.Notice, "收到一条通知，内容是$originContent")

        when (msg.kind) {
            APP_NOTICE_FOLLOW -> {
                val userInfo = UserReq.userInfo(UserInfoDto(msg.fromId)).await().map { info ->
                    val content = "${info.nickname}关注了你"
                    newNotice.content = content

                    NoticeMsgDB.insertOne(newNotice)
                    CuLog.info(CuTag.Notice, "收到一条通知，内容是$content")
                }
            }

            APP_NOTICE_NOT_FOLLOW -> {

            }


            APP_NOTICE_BLOCK -> {

            }

            APP_NOTICE_NOT_BLOCK -> {

            }

            APP_NOTICE_AT -> {
                val json = Json.decodeFromString(SocialNotice.serializer(), originContent)
                NoticeReq.find(
                    arrayOf(
                        NoticeReqDto(
                            fromId = msg.fromId, kind = msg.kind, sourceId = json.blogId
                        )
                    )
                ).await().map { info ->
                    val content = "${info.first().nickname}AT了你"
                    newNotice.sourceId = json.blogId

                    NoticeMsgDB.insertOne(newNotice)
                }
            }

            APP_NOTICE_AGREE_BLOG -> {
                val json = Json.decodeFromString(SocialNotice.serializer(), originContent)
                NoticeReq.find(
                    arrayOf(
                        NoticeReqDto(
                            fromId = msg.fromId, kind = msg.kind, sourceId = json.blogId
                        )
                    )
                ).await().map { info ->
                    val content = "${info.first().nickname}点赞了你的博文 ${
                        info.first().content.substring(
                            0, 10
                        )
                    }..."
                    newNotice.content = content
                    newNotice.sourceId = json.blogId

                    NoticeMsgDB.insertOne(newNotice)
                }
            }

            APP_NOTICE_AGREE_COMMENT -> {
                val json = Json.decodeFromString(SocialNotice.serializer(), originContent)
                NoticeReq.find(
                    arrayOf(
                        NoticeReqDto(
                            fromId = msg.fromId, kind = msg.kind, sourceId = json.blogId
                        )
                    )
                ).await().map { info ->
                    val content = "${info.first().nickname}点赞了你的评论"
                    newNotice.content = content

                    NoticeMsgDB.insertOne(newNotice)
                }
            }

            APP_NOTICE_AGREE_SUB_COMMENT -> {
                val json = Json.decodeFromString(SocialNotice.serializer(), originContent)
                NoticeReq.find(
                    arrayOf(
                        NoticeReqDto(
                            fromId = msg.fromId, kind = msg.kind, sourceId = json.blogId
                        )
                    )
                ).await().map { info ->
                    val content = "${info.first().nickname}点赞了你在博文${
                        info.first().content.substring(
                            0, 3
                        )
                    }下的评论"
                    newNotice.content = content

                    NoticeMsgDB.insertOne(newNotice)
                }
            }

            APP_NOTICE_REPLY_COMMENT -> {
                val json = Json.decodeFromString(SocialNotice.serializer(), originContent)
                NoticeReq.find(
                    arrayOf(
                        NoticeReqDto(
                            fromId = msg.fromId, kind = msg.kind, sourceId = json.blogId
                        )
                    )
                ).await().map { info ->
                    val content = "${info.first().nickname}回复了你"
                    newNotice.content =
                        NoticeContent(commentId = json.commentId, comment = json.comment).toString()

                    NoticeMsgDB.insertOne(newNotice)
                }.errMap {
                    CuLog.error(CuTag.Notice, it.msg)
                }
            }

            APP_NOTICE_REPLY_SUB_COMMENT -> {
                val json = Json.decodeFromString(SocialNotice.serializer(), originContent)
                NoticeReq.find(
                    arrayOf(
                        NoticeReqDto(
                            fromId = msg.fromId, kind = msg.kind, sourceId = json.blogId
                        )
                    )
                ).await().map { info ->
                    val content = "${info.first().nickname}回复了你在下的评论"
                    newNotice.content =
                        NoticeContent(commentId = json.commentId, comment = json.comment).toString()

                    NoticeMsgDB.insertOne(newNotice)
                }
            }

            APP_NOTICE_BLOG_CO_CREATE -> {

            }

            APP_NOTICE_REAL -> {
                val content = "您的实名认证已通过"

                NoticeMsgDB.insertOne(newNotice)
            }

            APP_NOTICE_ALBUM -> {

            }

            ///////////////////////////// 系统相关 ////////////////////////
            // {"id":1413,"text":"你的动态未审核通过,我们会进一步进行审核,原因:"}

            SYSTEM_NOTICE,
            SYSTEM_PASSED,
            UPDATE_AUTHORITY
            -> {
                val json = Json.decodeFromString(SystemNotice.serializer(), originContent)
                SystemNoticeMsg.insertOne(SystemNoticeMsgPo().apply {
                    userId = UserStore.userInfo.id
                    kind = msg.kind
                    sourceId = json.id
                    time = msg.time
                    content = json.text
                })

                val content = json.text
            }

        }
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


