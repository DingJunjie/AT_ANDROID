package com.bitat.repository.http.service

import com.bitat.repository.dto.req.GetUserRealDto
import com.bitat.repository.http.Http

//用户实名认证
object UserRealReq {
    //用户认证
    suspend inline fun userReal(dto: GetUserRealDto) = Http.post<_, Unit>("${Http.HOST}/service/userReal/userReal", dto)

    //判断用户是否已认证
    suspend inline fun getUserReal() = Http.get<Byte>("${Http.HOST}/service/userExtra/getUserReal")
}