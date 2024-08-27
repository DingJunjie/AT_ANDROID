package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class BlogAgreeDto(
    var blogId: Long, //博文id
    var labels: IntArray, //标签id数组
    var ops: Byte //详情见blog常量
)

@Serializable
class GetBlogAtDto(
    var atUserIds: LongArray, //at用户的id数组
    var labels: IntArray, //博文label数组
    var blogId: Long //博文id
)

@Serializable
class BlogOpsAgreeHistoryDto(
    var pageSize: Int, //条数
    var lastTime: Long = 0 //最后一条点赞时间
)

@Serializable
class BlogOpsAddCollectDto(
    var key: Int, //收藏夹key
    var blogId: Long //博文id
)

@Serializable
class BlogOpsRemoveCollectDto(
    var blogId: Long //博文id
)

@Serializable
class BlogOpsNotInterestedDto(
    var labelIds: IntArray //博文label数组
)
@Serializable
class TopBlogDto(
    var BlogId: Long,
    var Cover : String
)

class DelTopBlogDto(
    var BlogId: Long
)