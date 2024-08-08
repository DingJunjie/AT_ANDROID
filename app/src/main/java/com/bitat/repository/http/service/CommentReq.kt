package com.bitat.repository.http.service

import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http


//评论相关
object CommentReq {
    //创建一级评论
    suspend inline fun create(dto: BlogCreateCommentDto) = Http.post<_, Unit>("${Http.HOST}/service/comment/create", dto)

    //查询一级评论
    suspend inline fun find(dto: BlogFindCommentDto) = Http.post<_, CommentPart2Dto>("${Http.HOST}/service/comment/find", dto)

    //创建二级评论
    suspend inline fun createSub(dto: BlogCreateSubCommentDto) = Http.post<_, Unit>("${Http.HOST}/service/comment/createSub", dto)

    //查询二级评论
    suspend inline fun findSub(dto: BlogFindSubCommentDto) = Http.post<_, Array<SubCommentPartDto>>("${Http.HOST}/service/comment/findSub", dto)

    //删除评论
    suspend inline fun delete(dto: DeleteCommentDto) = Http.post<_, Unit>("${Http.HOST}/service/comment/delete", dto)

    //通过id获取评论
    suspend inline fun notice(dto: NoticeCommentDto) = Http.post<_, CommentPart1Dto>("${Http.HOST}/service/comment/notice", dto)
}