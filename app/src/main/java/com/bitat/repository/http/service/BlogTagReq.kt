package com.bitat.repository.http.service

import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

//用户标签
object BlogTagReq {
    //查询用户标签
    suspend inline fun find(dto: BlogTagFindDto) =
        Http.post<_, Array<BlogTagDto>>(
            "${Http.HOST}/service/blogTag/find",
            dto
        )

}