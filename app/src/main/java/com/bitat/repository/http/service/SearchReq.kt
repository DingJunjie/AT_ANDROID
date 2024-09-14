package com.bitat.repository.http.service

import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http


//搜搜相关
object SearchReq {
    //搜索联想
    suspend inline fun searchGuess(dto: SearchGuessDto) = Http.post<_, Array<String>>("${Http.HOST}/service/search/searchGuess", dto)

    //搜索
    suspend inline fun search(dto: SearchCommonDto) = Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/search/search", dto)

    //搜索用户
    suspend inline fun searchUser(dto: SearchCommonDto) = Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/search/searchUser", dto)

    //搜索视频
    suspend inline fun searchVideo(dto: SearchCommonDto) = Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/search/searchVideo", dto)

    //搜索推荐
    suspend inline fun recommendSearch(dto: RecommendSearchDto) = Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/search/recommendSearch", dto)

    //搜索推荐详情
    suspend inline fun recommendSearchDetail(dto: RecommendSearchDetailDto) = Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/search/recommendSearchDetail", dto)

    //通过tag进行搜索
    suspend inline fun findSearchTag(dto: FindSearchTagDto) = Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/search/findSearchTag", dto)
}