package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable


@Serializable
class CommentOperationDto(
    var blogId: Int, //博文id
    var commentId: Int, //评论id
    var ops: Int, // 是否点赞,详情看常量
    var kind: Int //评论类型,详情见常量
)