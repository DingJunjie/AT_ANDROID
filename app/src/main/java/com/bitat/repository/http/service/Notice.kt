package com.bitat.repository.http.service

import com.bitat.repository.dto.req.NoticeReqDto
import com.bitat.repository.dto.resp.NoticeDto
import com.bitat.repository.http.Http

object Notice {
    //获取通知
    suspend inline fun find(dto: Array<NoticeReqDto>) =
        Http.post<_, Array<NoticeDto>>("${Http.HOST}/service/notice/rendering", dto)
}