package com.bitat.repository.http.service

import com.bitat.repository.dto.req.CreateUserStateDto
import com.bitat.repository.http.Http

// 用户申诉
object UserStateReq {
    //新增
    suspend inline fun create(dto: CreateUserStateDto) =
        Http.post<_, Unit>("${Http.HOST}/service/userState/create", dto)
}