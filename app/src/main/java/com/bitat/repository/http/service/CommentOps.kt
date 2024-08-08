package com.bitat.repository.http.service


import com.bitat.repository.dto.req.*
import com.bitat.repository.http.Http

//操作评论
object CommentOps {
    //点赞评论
    suspend inline fun agree(dto: CommentOperationDto) =
        Http.post<_, Unit>("${Http.HOST}/service/commentOps/agree", dto)
}