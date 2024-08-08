package com.bitat.repository.http.service

import com.bitat.dto.resp.BlogPartDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

//博文操作相关
object BlogOpsReq {
    //点赞博文
    suspend inline fun agree(dto: BlogAgreeDto) = Http.post<_, Unit>(
        "${Http.HOST}/service/blogOps/agree", dto
    )

    //at博文
    suspend inline fun blogAt(dto: BlogAtDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blogOps/blogAt", dto)

    //点赞历史
    suspend inline fun agreeHistory(dto: BlogOpsAgreeHistoryDto) =
        Http.post<_, Array<BlogPartDto>>(
            "${Http.HOST}/service/blogOps/agreeHistory", dto
        )


    //添加收藏
    suspend inline fun addCollect(dto: BlogOpsAddCollectDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blogOps/addCollect", dto)

    //删除收藏夹中文博文
    suspend inline fun removeCollect(dto: BlogOpsRemoveCollectDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blogOps/removeCollect", dto)

    //不感兴趣
    suspend inline fun notInterested(dto: BlogOpsNotInterestedDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blogOps/notInterested", dto)
}