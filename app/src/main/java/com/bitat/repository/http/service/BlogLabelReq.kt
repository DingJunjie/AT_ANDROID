package com.bitat.repository.http.service

import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http


//博文标签
object BlogLabelReq {
    //查询标签
    suspend inline fun find() =  Http.get<Array<BlogLabelDto>>("${Http.HOST}/service/blogLabel/find")
}