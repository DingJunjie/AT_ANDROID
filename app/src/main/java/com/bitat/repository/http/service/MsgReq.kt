package com.bitat.repository.http.service

import com.bitat.repository.dto.req.FetchChatCommon
import com.bitat.repository.dto.resp.CountDto
import com.bitat.repository.dto.resp.KeySecretDto
import com.bitat.repository.dto.resp.LoginResDto
import com.bitat.repository.http.Http
import com.bitat.repository.http.HttpPb
import com.bitat.repository.singleChat.MsgDto.ChatMsgList
import com.bitat.repository.singleChat.MsgDto.NoticeMsgList

object MsgReq {
    suspend inline fun secret() =
        Http.get<KeySecretDto>("${Http.HOST}/msgMaster/msg/secret")
    suspend inline fun count() =
        Http.get<CountDto>("${Http.HOST}/msgMaster/msg/count")
    suspend inline fun fetchChat(dto: FetchChatCommon) =
        HttpPb.post(ChatMsgList::parseFrom,"${Http.HOST}/msgMaster/msg/fetchChat", dto)
    suspend inline fun fetchNotice(dto: FetchChatCommon) =
        HttpPb.post(NoticeMsgList::parseFrom,"${Http.HOST}/msgMaster/msg/fetchNotice", dto)
}