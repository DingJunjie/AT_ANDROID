package com.bitat.ext

import com.bitat.repository.consts.CHAT_Picture
import com.bitat.repository.consts.CHAT_Recall
import com.bitat.repository.consts.CHAT_Text
import com.bitat.repository.consts.CHAT_Video

fun String.toChatMessage(kind: Short): String {
    return when (kind) {
        CHAT_Text -> this
        CHAT_Picture -> "[图片]"
        CHAT_Video -> "[视频]"
        CHAT_Recall -> "撤回了一条消息"
        else -> this
    }
}