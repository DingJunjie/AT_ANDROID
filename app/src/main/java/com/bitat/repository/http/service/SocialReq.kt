package com.bitat.repository.http.service

import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.http.Http

//社交操作
object SocialReq {
    //关注用户
    suspend inline fun follow(dto: SocialDto) = Http.post<_, Int>("${Http.HOST}/service/social/follow", dto)

    //拉黑用户
    suspend inline fun block(dto: SocialDto) = Http.post<_, Int>("${Http.HOST}/service/social/block", dto)
}