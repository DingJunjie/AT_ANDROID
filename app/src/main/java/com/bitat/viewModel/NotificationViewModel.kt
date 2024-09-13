package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.consts.APP_NOTICE_REPLY_COMMENT
import com.bitat.repository.dto.req.NoticeReqDto
import com.bitat.repository.dto.resp.NoticeDto
import com.bitat.repository.http.service.NoticeReq
import com.bitat.repository.po.NoticeMsgPo
import com.bitat.repository.singleChat.NoticeContent
import com.bitat.repository.singleChat.SocialNotice
import com.bitat.repository.sqlDB.NoticeMsgDB
import com.bitat.repository.store.UserStore
import com.bitat.state.Notification
import com.bitat.state.NotificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class NotificationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> get() = _state.asStateFlow()

    fun getNotifications() {
        MainCo.launch {
            val res = NoticeMsgDB.find(UserStore.userInfo.id)
            val req = res.map {
                NoticeReqDto(fromId = it.fromId, kind = it.kind, sourceId = it.sourceId)
            }.toTypedArray()

            val that = NoticeReq.find(req).await().get()
            val deal = that.mapIndexed { i, v ->
                getContent(res[i], v)
            }

            _state.update {
                it.notifications.clear()
                it.notifications.addAll(deal)
                it
            }

        }
    }

    suspend fun getContent(notice: NoticeMsgPo, info: NoticeDto): Notification {
        when (notice.kind) {
            APP_NOTICE_REPLY_COMMENT -> {
                val json = Json.decodeFromString(NoticeContent.serializer(), notice.content)
                return Notification().apply {
                    id = notice.id
                    fromId = notice.fromId
                    kind = notice.kind
                    sourceId = notice.sourceId
                    fromId = notice.fromId
                    time = notice.time
                    content = notice.content
                    displayContent = "${info.nickname} 回复了你 ${json.comment}"
                    comment = json.comment
                    commentId = json.commentId
                    nickname = info.nickname
                    profile = info.profile
                    cover = info.cover
                }
            }

            else ->
                return Notification().apply {
                    id = notice.id
                    fromId = notice.fromId
                    kind = notice.kind
                    sourceId = notice.sourceId
                    fromId = notice.fromId
                    time = notice.time
                    content = notice.content
                    displayContent = notice.content
                    nickname = info.nickname
                    profile = info.profile
                    cover = info.cover
                }
        }
    }

    init {
        getNotifications()
    }
}