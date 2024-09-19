package com.bitat.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLACKLIST
import com.bitat.repository.consts.CHAT_DISABLED
import com.bitat.repository.consts.CHAT_Recall
import com.bitat.repository.consts.CHAT_Reply
import com.bitat.repository.consts.CHAT_Time
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SocialRelDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatDetailsState
import com.bitat.state.RecallMessageParams
import com.bitat.state.ReplyMessageParams
import com.bitat.state.VideoMessageParams
import com.bitat.ui.common.rememberToastState
import com.bitat.utils.FileType
import com.bitat.utils.ImageUtils
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.TimeUtils
import com.bitat.utils.UPLOAD_OPS
import com.bitat.utils.VideoUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.sql.Time
import java.util.concurrent.atomic.AtomicBoolean

const val MIN_DIFF_TIMESTAMP = 5 * 60 * 1000

enum class ReplyType {
    StrangerSender, StrangerRecipient, StrangerEnable, Blocked, Normal
}

/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
class ChatDetailsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatDetailsState())
    val state: StateFlow<ChatDetailsState> get() = _state.asStateFlow()

    fun getHistoryByKeyword(toId: Long, keyword: String) {
        MainCo.launch(IO) {
            val result = SingleMsgDB.findContentByLike(UserStore.userInfo.id, toId, keyword)
            _state.update {
                it.historyMsgList.clear()
                it.historyMsgList.addAll(result)
                it
            }
        }
    }

    fun getMessage(toId: Long, pageSize: Int = 10, pageNo: Int = 0, completeFn: () -> Unit = {}) {

        val cs = canSend(toId)

        if (cs == ReplyType.Normal || cs == ReplyType.StrangerEnable) {
            val msg = SingleMsgDB.findMsg(UserStore.userInfo.id, toId, pageNo, pageSize)
//        SingleMsgDB.filterDuplicate(UserStore.userInfo.id, toId, msg)
            println("search msg")
            println(msg.toString())
            val newList = arrayListOf<SingleMsgPo>()

            msg.forEachIndexed { i, v ->
//                if ((
//                            //大于0，避免最下面有时间，且当前的kind不是时间
//                            i > 0 && v.kind != CHAT_Time
//                                    && (
//                                    // 上一个距离这个差n分钟
//                                    // 上一个也不是时间
//                                    msg[i - 1].time - v.time > MIN_DIFF_TIMESTAMP
//                                            && (i > 1 && msg[i - 1].kind != CHAT_Time)))
//                    // 或者最后一个
//                    || i == msg.lastIndex
//                ) {
//                    val timeMsg = v
//                    timeMsg.kind = CHAT_Time
//                    timeMsg.content = TimeUtils.timeToMD(v.time)
//
//                    newList.add(i - 1, timeMsg)
//                }
                newList.add(v)
//                if (i == msg.lastIndex) {
//                    val timeMsg = v
//                    timeMsg.kind = CHAT_Time
//                    timeMsg.content = TimeUtils.timeToMD(v.time)
//
//                    newList.add(v)
//                }
            }

            _state.update {
                if (pageNo == 0) {
                    it.messageList.clear()
                }
//            it.messageList.addAll(msg.first())
                it.messageList.addAll(newList)
                it.copy(currentPage = pageNo + 1)
            }
        } else if (cs == ReplyType.StrangerSender || cs == ReplyType.StrangerRecipient) {
            val msg = SingleMsgDB.findMsg(UserStore.userInfo.id, toId, pageNo, pageSize)

            val newMsg = SingleMsgPo().apply {
                kind = CHAT_DISABLED
                content =
                    if (cs == ReplyType.StrangerSender) "您和对方并非好友，在对方回复前只能发送一条消息" else "对方与您并非好友，在您回复前对方只能发送一条消息"
                time = TimeUtils.getNow()
                status = if (cs == ReplyType.StrangerSender) 1 else -1
            }
            _state.update {
                if (pageNo == 0) {
                    it.messageList.clear()
                }
                it.messageList.addAll(msg)
                it.messageList.add(0, newMsg)
                it
            }
        } else if (cs == ReplyType.Blocked) {
            val msg = SingleMsgDB.findMsg(UserStore.userInfo.id, toId, pageNo, pageSize)
            val newMsg = SingleMsgPo().apply {
                kind = CHAT_DISABLED
                content = "您已将对方拉黑"
                time = TimeUtils.getNow()
                status = 1
            }
            _state.update {
                if (pageNo == 0) {
                    it.messageList.clear()
                }
                it.messageList.addAll(msg)
                it.messageList.add(0, newMsg)
                it
            }
        }
    }

    fun getNewMessage(newMsg: SingleMsgPo) {
        if (newMsg.kind == CHAT_Recall) {
            _state.update {
                val originTime =
                    Json.decodeFromString(RecallMessageParams.serializer(), newMsg.content)
                val i = it.messageList.indexOfFirst { that ->
                    originTime.time == that.time
                }
                if (i > -1) {
                    val oldMsg = it.messageList.removeAt(i)
                    oldMsg.kind = CHAT_Recall
                    it.messageList.add(i, oldMsg)
                }
                it
            }
        } else {
            _state.update {
                it.messageList.add(0, newMsg)
                it
            }
        }
    }

    fun setReplyMsg(msg: SingleMsgPo) {
        _state.update {
            it.copy(replyMsg = msg)
        }
    }

    fun recallMessage(msg: SingleMsgPo) {
        val rmp = RecallMessageParams(time = msg.time)
        val content = Json.encodeToString(RecallMessageParams.serializer(), rmp)

        sendMessage(toId = msg.otherId, kind = CHAT_Recall.toInt(), content, {}, {})

        MainCo.launch(IO) {
            SingleMsgDB.updateKind(CHAT_Recall, msg.selfId, msg.otherId, msg.time)
        }

        _state.update {
            val i = it.messageList.indexOf(msg)
            val oldData = it.messageList.removeAt(i)
            oldData.kind = CHAT_Recall

            it.messageList.add(i, oldData)
            it
        }
    }

    fun deleteMessage(msg: SingleMsgPo) {
        _state.update {
            it.messageList.remove(msg)
            it
        }

        MainCo.launch(IO) {
            SingleMsgDB.deleteByTime(msg.selfId, msg.otherId, msg.time)
        }
    }

    /**
     * 判断是否可以发送
     * 如果拉黑了不可以发送
     * 如果没查到就是陌生人，再去查消息表
     * 陌生人之间 一来一回后可以聊天一段时间，如果一段时间后没聊了可以再发一条
     */
    fun canSend(toId: Long): ReplyType {
        val res = SocialRelDB.getRel(UserStore.userInfo.id, toId)
        if (res.toInt() == BLACKLIST) {
            // 拉黑
            return ReplyType.Blocked
        } else {
            // 陌生人
            val msgList = SingleMsgDB.findMsg(UserStore.userInfo.id, toId, 0, 30)
            // 如果我有回复过(status > 1)那随便发
            val sentBeforeIndex = msgList.indexOfFirst {
                it.status > 0
            }
            val receiveBeforeIndex = msgList.indexOfFirst {
                it.status < 0
            }
            if (sentBeforeIndex > -1 && receiveBeforeIndex > -1) {
                // 我回复过
                return ReplyType.StrangerEnable
            } else if (sentBeforeIndex > -1) {
                // 发送过，没收过
                return ReplyType.StrangerSender
            } else if (receiveBeforeIndex > -1) {
                // 收到过，没发过
                return ReplyType.StrangerRecipient
            } else {
                return ReplyType.Normal
            }
        }
    }

    fun sendMessage(
        toId: Long,
        kind: Int,
        content: String,
        showToast: (String) -> Unit = {},
        completeFn: (SingleMsgPo) -> Unit
    ) {
        val cs = canSend(toId)

        when (cs) {
            ReplyType.StrangerSender -> {
                // 无法发送
                showToast("对方还未回复您，无法发送消息")
                return
            }

            ReplyType.StrangerRecipient -> {
                // 发送后就变成了可以发送的普通陌生人
            }

            ReplyType.Blocked -> {
                // 拉黑了，无法发送，报错
                showToast("您已拉黑对方，无法发送")
                return
            }

            ReplyType.StrangerEnable,
            ReplyType.Normal -> {
                // 可以发送
            }
        }

        var c = content
        var k = kind
        if (state.value.replyMsg != null) {
            val replyMsg = ReplyMessageParams(
                time = state.value.replyMsg!!.time,
                replyMsg = state.value.replyMsg!!.content,
                kind = state.value.replyMsg!!.kind,
                content = content
            )
            c = Json.encodeToString(ReplyMessageParams.serializer(), replyMsg)
            k = CHAT_Reply.toInt()
        }

        val time = TimeUtils.getNow()

        if (k != CHAT_Recall.toInt()) {

            val msg = SingleMsgPo()
            msg.kind = k.toShort()
            msg.content = c
            msg.time = time
            msg.selfId = UserStore.userInfo.id
            msg.status = 1
            msg.otherId = toId
            SingleMsgDB.insertOne(msg)

            _state.update {
//            if(it.messageList[0].time )
                it.messageList.add(0, msg)
                it.copy(replyMsg = null)
            }
            TcpClient.chat(toId, k, time, c.toByteArray(charset("UTF-8")))
            completeFn(msg)
        } else {
            TcpClient.chat(toId, k, time, c.toByteArray(charset("UTF-8")))
        }

    }

    fun sendPicture(
        toId: Long,
        uri: Uri,
        showToast: (String) -> Unit = {},
        completeFn: (SingleMsgPo) -> Unit
    ) {
        MainCo.launch(IO) {
            QiNiuUtil.uploadSingleFile(uri, ops = UPLOAD_OPS.Chat, fileType = FileType.Image) {
                sendMessage(toId, 2, content = it, showToast, completeFn)
            }
        }
    }

    fun sendVideo(toId: Long, uri: Uri, completeFn: (SingleMsgPo) -> Unit) {
        MainCo.launch(IO) {
            val cover = VideoUtils.getCover(uri)

            QiNiuUtil.uploadSingleFile(
                cover, UPLOAD_OPS.Chat, FileType.Image, isUsingUri = false
            ) { coverKey ->
                QiNiuUtil.uploadSingleFile(
                    uri, UPLOAD_OPS.Chat, FileType.Video
                ) { key ->
                    val m = VideoMessageParams(cover = coverKey, video = key)
                    val content = Json.encodeToString(VideoMessageParams.serializer(), m)

                    sendMessage(toId, kind = 3, content) {
                        completeFn(it)
                    }
                }
            }
        }
    }
}