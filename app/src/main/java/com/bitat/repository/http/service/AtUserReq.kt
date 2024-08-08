package com.bitat.repository.http.service

import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

//用户at相关
object AtUserReq {
    //邀请用户注册路由
    suspend inline fun atOutUser(dto: AtOutUserDto) = Http.post<_, String>(
        "${Http.HOST}/service/atUser/atOutUser", dto
    )


    //外部查看被at的博文
    suspend inline fun atGetBlog(dto: AtGetBlogDto) = Http.post<_, BlogAtDto>(
        "${Http.HOST}/service/atUser/atGetBlog", dto
    )


    //查看谁at了我
    suspend inline fun getAtUser(dto: GetAtUserDto) = Http.post<_, AtUserPart2Dto>(
        "${Http.HOST}/service/atUser/getAtUser", dto
    )
}
