package com.bitat.repository.http.service

import com.bitat.repository.dto.req.CreateUserReportDto
import com.bitat.repository.http.Http


// 用户举报信息相关路由
object UserReportReq {

    //新增举报(通过举报类型)
    suspend inline fun createReport(dto: CreateUserReportDto) =
        Http.post<_, Unit>("${Http.HOST}/service/userReport/createReport", dto)
}