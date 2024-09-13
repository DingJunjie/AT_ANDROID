package com.bitat.repository.http.service

import com.bitat.repository.dto.common.PageDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.RankingDto
import com.bitat.repository.http.Http


//榜单
object RankingReq {

    //获取榜单
    suspend inline fun rankingList(page: PageDto) =
        Http.post<PageDto, Array<RankingDto>>("${Http.HOST}/service/ranking/list",
            page)

}

