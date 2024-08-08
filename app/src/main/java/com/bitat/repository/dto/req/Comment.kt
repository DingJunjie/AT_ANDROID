package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable
import  com.bitat.repository.dto.common.AtUserDto
import  com.bitat.repository.dto.common.ResourceDto

@Serializable
class BlogCreateCommentDto(
    var blogId: Long, //博文id
    var content: String, //内容
    var labels: IntArray, //博文label数组
    var resource: com.bitat.repository.dto.common.ResourceDto, //评论资源
    var atUsers: Array<com.bitat.repository.dto.common.AtUserDto> //at用户数组
)

@Serializable
class BlogFindCommentDto(
    var blogId: Long, //博文id
    var lastId: Long, //最后一条id
    var pageSize: Int,
)

@Serializable
class BlogCreateSubCommentDto(
    var blogId: Long, //博文id
    var pId: Long, //父id
    var toUserId: Long, //回复用户id
    var content: String, //内容
    var resource: com.bitat.repository.dto.common.ResourceDto, //资源
    var atUsers: Array<com.bitat.repository.dto.common.AtUserDto>, //at用户
    var labels: IntArray //博文label数组
)

@Serializable
class BlogFindSubCommentDto(
    var pageSize: Int, //条数
    var pId: Long = 0, //一级评论id
    var lastId: Long = 0 //最后一条id
)

@Serializable
class DeleteCommentDto(
    var blogId: Long, //博文id
    var userId: Long, //用户id
    var commentId: Long, //评论id
    var kind: Byte  // 一级评论还是耳机,详情见comment常量
)

@Serializable
class NoticeCommentDto(
    var sourceId: Long, //评论id
    var operate: Byte  // 通知类型,详情见常量
)