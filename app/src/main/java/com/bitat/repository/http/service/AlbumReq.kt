package com.bitat.repository.http.service

import com.bitat.dto.resp.BlogBaseDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.http.*
import com.bitat.repository.http.Http

//专辑相关请求
object AlbumReq {
    //查询专辑对应的博文
    suspend fun query(dto: AlbumQueryDto) =
        Http.post<_, Array<BlogBaseDto>>(
            "${Http.HOST}/service/album/query", dto
        )


    //删除专辑对应的博文
    suspend inline fun deleteBlog(dto: DeleteAlbumBlogDto) =
        Http.post<_, Unit>(
            "${Http.HOST}/service/album/deleteBlog", dto
        )


    //删除专辑
    suspend inline fun delete(dto: DeleteAlbumDto) =
        Http.post<_, Unit>(
            "${Http.HOST}/service/album/delete", dto
        )


    //将动态放进专辑中
    suspend inline fun insert(dto: BlogInsertAlbumDto) =
        Http.post<_, Unit>(
            "${Http.HOST}/service/album/insert", dto
        )


    //打开跟动态
    suspend inline fun open(dto: OpenAlbumDto) = Http.post<_, Unit>(
        "${Http.HOST}/service/album/open", dto
    )
}
