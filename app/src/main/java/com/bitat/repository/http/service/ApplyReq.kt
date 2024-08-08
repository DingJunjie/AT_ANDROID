package com.bitat.repository.http.service

import com.bitat.repository.dto.req.*
import com.bitat.repository.http.*
import com.bitat.repository.http.Http

//申请权限相关
object ApplyReq {
    //申请通用权限
    suspend inline fun create(dto: CreateApplyDto) =
        Http.post<_, Unit>("${Http.HOST}/service/apply/create", dto)

    //申请美丽乡村权限
    suspend inline fun createRustic(dto: CreateRusticApplyDto) =
        Http.post<_, Unit>("${Http.HOST}/service/apply/createRustic", dto)
}