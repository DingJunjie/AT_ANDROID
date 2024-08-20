package com.bitat.repository.http.service

import com.bitat.dto.resp.BlogBaseDto
import com.bitat.dto.resp.BlogPart2Dto
import com.bitat.dto.resp.BlogPart3Dto
import com.bitat.dto.resp.BlogPartDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

//博文相关路由
object BlogReq {
    //发布博文
    suspend inline fun publish(dto: PublishBlogDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blog/publish", dto)

    //删除博文
    suspend inline fun delete(dto: DeleteBlogDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blog/delete", dto)

    //修改博文可见性和是否可评论
    suspend inline fun editVisible(dto: EditVisibleDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blog/editVisible", dto)

    //通过id查询博文
    suspend inline fun getBlog(dto: GetBlogDto) =
        Http.post<_, BlogBaseDto>("${Http.HOST}/service/blog/getBlog", dto)


    //查看最新发布
    suspend inline fun newBlogs(dto: NewBlogsDto) =
        Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/blog/newBlogs", dto)


    //查看最热博文
    suspend inline fun hotBlogs(dto: HotBlogsDto) =
        Http.post<_, Array<BlogPart3Dto>>("${Http.HOST}/service/blog/hotBlogs", dto)


    //查看关注对象发布的博文
    suspend inline fun followBlogs(dto: FollowBlogsDto) =
        Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/blog/followBlogs", dto)


    //查看同城
    suspend inline fun sameCityBlogs(dto: SameCityBlogsDto) =
        Http.post<_, Array<BlogPart2Dto>>("${Http.HOST}/service/blog/sameCityBlogs", dto)

    //获取推荐博文
    suspend inline fun recommendBlogs() =
        Http.get<Array<BlogBaseDto>>("${Http.HOST}/service/blog/recommendBlogs")

    //用过blogId数组查询博文封面信息
    suspend inline fun queryCover(dto: QueryCoverDto) =
        Http.post<_, Array<BlogPartDto>>("${Http.HOST}/service/blog/queryCover", dto)


    //获取我的是时间线
    suspend inline fun timeLine(dto: TimeLineDto) =
        Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/blog/timeLine", dto)


    //用户加入共创
    suspend inline fun addCoCreates(dto: AddCoCreatesDto) =
        Http.post<_, Unit>("${Http.HOST}/service/blog/addCoCreates", dto)

    //博文其他信息
    suspend inline fun blogOther(dto: BlogOtherDto) =
        Http.post<_, BlogAlbumDto>("${Http.HOST}/service/blog/blogOther", dto)


    //通过blogIds查询博文信息
    suspend inline fun find(dto: BlogFindDto) =
        Http.post<_, Array<BlogBaseDto>>("${Http.HOST}/service/blog/find", dto)


}