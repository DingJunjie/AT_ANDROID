package com.bitat.repository.http.service

import com.bitat.repository.dto.common.PageDto
import com.bitat.repository.dto.common.T2
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.http.Http


//榜单
object RankingReq {

    //获取榜单
    suspend inline fun rankingList(page: PageDto) =
        Http.post<PageDto, Array<T2<Byte, Array<BlogPartDto>>>>("${Http.HOST}/service/ranking/list",
            page)

}