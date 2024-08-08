package com.bitat.repository.http.service

import com.bitat.repository.dto.req.CreateFeedBackDto
import com.bitat.repository.http.Http

//用户反馈
object FeedBackReq {
    //用户创建反馈
    suspend inline fun create(dto: CreateFeedBackDto) =
        Http.post<_, Unit>("${Http.HOST}/service/feedBack/create", dto)
}