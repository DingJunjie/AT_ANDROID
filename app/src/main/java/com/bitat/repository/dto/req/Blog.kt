package  com.bitat.repository.dto.req

import com.bitat.repository.consts.BLOG_COMMENTABLE_ALL
import com.bitat.repository.consts.BLOG_NOT_EDIT
import com.bitat.repository.consts.BLOG_VISIBLE_ALL
import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.dto.common.TagsDto
import com.bitat.utils.EmptyArray
import kotlinx.serialization.Serializable

@Serializable
class PublishBlogDto(
    var albumOps: Long = 0,  //专辑id, -1:自己可跟随,-2:团队可跟随,-3:好友可跟随,-4粉丝可跟 -5所有人可跟随, 0：无操作 >0 跟随专辑id
    var musicId: Long = 0, ////音乐id
    var kind: Byte = 0,  //详情见blog常量
    var vote: Byte = 0, //0:不是,如果是请使用 BLOG_VOTE_YES 常量
    var cover: String = "",  //博文封面
    var adCode: String = "", //行政编码
    var visible: Byte = BLOG_VISIBLE_ALL.toByte(), //博文可见性 1、仅自己可见  2、仅好友可见  3、所有人可见
    var openComment: Byte = BLOG_COMMENTABLE_ALL.toByte(),  //详情见comment常量
    var longitude: Double = 0.0, //经度
    var latitude: Double = 0.0,//纬度
    var location: String = "", //本地位置
    var content: String = "", //动态文字内容
    var albumMembers: LongArray = EmptyArray.long, //专辑团队
    var tags: Array<TagsDto> = emptyArray(),//标签
    var resource: ResourceDto = ResourceDto(),//资源（json:有类型和文件key）
    var coCreates: LongArray = EmptyArray.long,//用户共创
    var atUsers: LongArray = EmptyArray.long//艾特用户
)

@Serializable
class DeleteBlogDto(
    var blogId: Long, //博文id
    var kind: Byte  //博文类型
)

@Serializable
class EditVisibleDto(
    var blogId: Long, //博文id
    var visible: Byte, //博文可见性(不修改为0)
    var openComment: Byte=BLOG_NOT_EDIT.toByte() //评论可见性(不修改为0)
)

@Serializable
class GetBlogDto(
    var id: Long //博文id
)

@Serializable
class NewBlogsDto(
    var pageSize: Int, //分页
    var lastHint: Long = 0 //最后一条博文创建时间
)

@Serializable
class HotBlogsDto(
    var pageSize: Int, //分页
    var lastHint: Long = 0 //最后一条博文创建时间
)

@Serializable
class FollowBlogsDto(
    var pageSize: Int, //分页
    var lastTime: Long = 0 //最后一条博文创建时间
)

@Serializable
class SameCityBlogsDto(
    var pageSize: Int, //条数
    var pageNo: Int, //几页
    var longitude: Double = 0.0, //经度
    var latitude: Double = 0.0 //纬度
)

@Serializable
class QueryCoverDto(
    var blogId: Long = 0L, //博文id
    var time:Long=0L
)



@Serializable
class TimeLineDto(
    var pageSize: Int, //条数
    var userId: Long = 0, //被查看的人的id
    var lastTime: Long = 0 //最后一条博文id
)

@Serializable
class AddCoCreatesDto(
    var blogId: Long //博文id
)

@Serializable
class BlogOtherDto(
    var blogId: Long, //博文id
    var albumId: Long // 专辑id
)

@Serializable
class BlogFindDto(
    var blogIds: IntArray = EmptyArray.int //博文id数组
)
