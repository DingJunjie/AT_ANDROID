package com.bitat.repo.httpReq.service


import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.req.FindMusicBlog
import com.bitat.repository.dto.req.GetCollect
import com.bitat.repository.dto.req.MusicCollect
import com.bitat.repository.dto.req.MusicFind
import com.bitat.repository.dto.req.MusicGet
import com.bitat.repository.dto.req.SearchMusic
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

object MusicReq {
    //获取音乐推荐
    suspend inline fun find(dto: MusicFind) = Http.post<_, Array<MusicPart>>("${Http.HOST}/service/music/find", dto)

    //收藏音乐
    suspend inline fun collect(dto: MusicCollect) = Http.post<_, Array<Unit>>("${Http.HOST}/service/music/collect", dto)

    //获取我收藏的音乐
    suspend inline fun getCollect(dto: GetCollect) = Http.post<_, Array<MusicPart>>("${Http.HOST}/service/music/getCollect", dto)

    //搜索音乐
    suspend inline fun search(dto: SearchMusic) = Http.post<_, Array<MusicPart>>("${Http.HOST}/service/music/search", dto)

    //使用这首音乐的博文
    suspend inline fun findMusicBlog(dto: FindMusicBlog) =
            Http.post<_, Array<BlogPartDto>>(
                "${Http.HOST}/service/music/findMusicBlog", dto
            )


    //通过id查询音乐
    suspend inline fun get(dto: MusicGet) = Http.post<_, MusicPart>("${Http.HOST}/service/music/get", dto)
}