package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class AlbumQueryDto(
    var albumId: Long,//专辑id
    var lastTime: Long, //最后一条创建时间
    var pageSize: Int //分页
)

@Serializable
class DeleteAlbumBlogDto(
    var albumId: Long, //专辑id
    var blogId: Long //博文id
)

@Serializable
class DeleteAlbumDto(
    var albumId: Long //专辑id
)

@Serializable
class BlogInsertAlbumDto(
    var albumId: Long, //专辑id
    var blogId: Long //博文id
)

@Serializable
class OpenAlbumDto(
    var blogId: Long,//博文id
    var albumOps: Long, //专辑id：-1，自己可跟随,-2:团队可跟随,-3:好友可跟随,-4粉丝可跟 -5所有人可跟随
    var albumId: Long,//专辑id
    var albumMembers: String,//专辑团队
    var tags: String, //专辑标签
    var cover: String //专辑封面
)