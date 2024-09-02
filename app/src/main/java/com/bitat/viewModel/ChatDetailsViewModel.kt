package com.bitat.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ChatDetailsState
import com.bitat.state.VideoMessageParams
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
import java.util.concurrent.atomic.AtomicBoolean


/**
 *    author : shilu
 *    date   : 2024/8/6  10:56
 *    desc   :
 */
class ChatDetailsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatDetailsState())
    val state: StateFlow<ChatDetailsState> get() = _state.asStateFlow()

    fun getMessage(toId: Long, pageSize: Int = 10, pageNo: Int = 0, completeFn: () -> Unit = {}) {
        val msg = SingleMsgDB.findMsg(UserStore.userInfo.id, toId, pageNo, pageSize)
        println("search msg")
        println(msg.toString())
        _state.update {
            if (pageNo == 0) {
                it.messageList.clear()
            }
            it.messageList.addAll(msg.first())
            it.copy(currentPage = pageNo + 1)
        }
    }

    fun getNewMessage(newMsg: SingleMsgPo) {
        _state.update {
            it.messageList.add(0, newMsg)
            it
        }
    }

    fun sendMessage(toId: Long, kind: Int, content: String, completeFn: (SingleMsgPo) -> Unit) {
        val msg = SingleMsgPo()
        msg.kind = kind.toShort()
        msg.content = content
        msg.time = TimeUtils.getNow()
        msg.selfId = UserStore.userInfo.id
        msg.status = 1
        msg.otherId = toId
        SingleMsgDB.insertOne(msg.selfId, msg.otherId, msg.status, msg.time, msg.kind, msg.content)

        _state.update {
            it.messageList.add(0, msg)
            it
        }

        TcpClient.chat(toId, kind, content.toByteArray(charset("UTF-8")))
        completeFn(msg)
    }

    fun sendPicture(toId: Long, uri: Uri, completeFn: (SingleMsgPo) -> Unit) {
        MainCo.launch(IO) {
            QiNiuUtil.uploadSingleFile(uri, ops = UPLOAD_OPS.Pub, fileType = FileType.Image) {
                sendMessage(toId, 2, content = it, completeFn)
            }
        }
    }

    fun sendVideo(toId: Long, uri: Uri, completeFn: (SingleMsgPo) -> Unit) {
        MainCo.launch(IO) {
            val cover = VideoUtils.getCover(uri.toString())

            QiNiuUtil.uploadSingleFile(
                cover,
                UPLOAD_OPS.Pub,
                FileType.Image,
            ) { coverKey ->
                QiNiuUtil.uploadSingleFile(
                    uri,
                    ops = UPLOAD_OPS.Pub,
                    fileType = FileType.Video
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