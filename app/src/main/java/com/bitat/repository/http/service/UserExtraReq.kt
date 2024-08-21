package com.bitat.repository.http.service

import com.bitat.dto.resp.BlogPartDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http


//用户额外信息
object UserExtraReq {
    //添加表情
    suspend inline fun addSticker(dto: AddStickerDto) = Http.post<_, Unit>("${Http.HOST}/service/userExtra/addSticker", dto)

    //查询表情
    suspend inline fun findStickers(dto: FindStickersDto) = Http.post<_, Array<String>>("${Http.HOST}/service/userExtra/findStickers", dto)

    //删除表情
    suspend inline fun deleteSticker(dto: DeleteStickerDto) = Http.post<_, Unit>("${Http.HOST}/service/userExtra/deleteSticker", dto)

    //新建收藏夹
    suspend inline fun createCollect(dto: CreateCollectDto) = Http.post<_, Unit>("${Http.HOST}/service/userExtra/createCollect", dto)

    //删除收藏夹
    suspend inline fun deleteCollect(dto: DeleteCollectDto) = Http.post<_, Unit>("${Http.HOST}/service/userExtra/deleteCollect", dto)

    //修改收藏夹
    suspend inline fun updateCollect(dto: UpdateCollectDto) = Http.post<_, Unit>("${Http.HOST}/service/userExtra/updateCollect", dto)

    //获取收藏夹列表
    suspend inline fun collectList() = Http.get<Array<CollectPartDto>>("${Http.HOST}/service/userExtra/collectList")

    //查询我收藏的所有作品
    suspend inline fun findCollectOpus(dto: FindCollectOpusDto) = Http.post<_, Array<BlogPartDto>>("${Http.HOST}/service/userExtra/findCollectOpus", dto)

    //查询收藏夹下的作品
    suspend inline fun collectNextList(dto: CollectNextListDto) = Http.post<_, Array<BlogPartDto>>("${Http.HOST}/service/userExtra/collectNextList", dto)

    //判断用户是否拥有某一个权限
    suspend inline fun isApply(dto: IsApplyDto) = Http.post<_, Unit>("${Http.HOST}/service/userExtra/isApply", dto)

}