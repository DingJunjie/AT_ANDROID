package com.bitat.repository.http.service

import com.bitat.repository.dto.resp.KeySecretDto
import com.bitat.repository.http.Http

object MsgReq {
    suspend inline fun secret() =
        Http.get<KeySecretDto>("${Http.HOST}/msgMaster/msg/secret")
}