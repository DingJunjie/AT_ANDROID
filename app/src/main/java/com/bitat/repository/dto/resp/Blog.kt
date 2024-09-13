package  com.bitat.repository.dto.resp

import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.dto.common.TagsDto
import com.bitat.utils.EmptyArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class BlogBaseDto {
    var id: Long = 0 //博文id
    var userId: Long = 0//用户id
    var albumId: Long = 0 //专辑id
    var musicId: Long = 0//音乐id
    var agrees: UInt = 0u //点赞数
    var comments: UInt = 0u //评论数
    var ats: UInt = 0u //艾特数
    var collects: UInt = 0u //收藏数
    var visible: Byte = 0 //可见性
    var openComment: Byte = 0 //是否开放评论
    var kind: Byte = 0 //博文类型
//    var musicKind: Byte = 0 //音乐类型
    var status: Byte = 0 //状态
    var vote: Byte = 0//投票
    var cover: String = "" //封面
    var longitude: Double = 0.0 //经度
    var latitude: Double = 0.0//纬度
    var ipTerritory: String = ""//ip属地
    var location: String = "" //本地位置
    var adCode: String = "" // 邮政编号
    var content: String = ""//文字内容
    var labels: IntArray = EmptyArray.int //标签
    var tags: Array<TagsDto> = emptyArray() //用户标签
    var resource: ResourceDto = ResourceDto() //资源
    var coCreates: String = "" //共创
    var createTime: Long = 0 //发布时间
    var coCreateInfos: Array<UserBaseDto> = emptyArray()//共创用户信息
    var blogVote: BlogVotesDto = BlogVotesDto() //投票
    var nickname: String = "" //用户名
    var profile: String = "" //头像
    var gender: Byte = 0 //性别
    var hasPraise: Boolean = false//是否点赞
    var hasCollect: Boolean = false //是否收藏
    var hasAt: Boolean = false //是否at
    var rel: Int = 0 //正向关系  -1 1
    var revRel: Int = 0//反向关系
    var alias: String = "" //别名
    var atCount: Int = 0 //被at数
    var exposure: Int = 0//爆光度
    var musicInfo: MusicPart1? = null //音乐信息

    //--------------------------------------------
    @Transient //表示不参加序列化
    var out: Int = 0
    @Transient
    var height: Int = 0
}

@Serializable
class BlogPartDto(
    var id: Long = 0L,
    var userId: Long = 0L,
    var nickname: String = "",
    var profile: String = "", // 用户头像
    var visible: Byte = 0,
    var agrees: UInt = 0u,
    var status: Int = 0,
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var comments: UInt = 0u,
    var ats: UInt = 0u,
    var exposure: Int = 0,
    var kind: Byte = 0,
    var cover: String = "",
    var content: String = "",
    var createTime: Long = 0L,
    var vote: Byte = 0,
    var labels: IntArray = EmptyArray.int,
    var tags: Array<TagsDto> = arrayOf(),
    var resource: ResourceDto = ResourceDto(),
    var blogVote: BlogVotesDto = BlogVotesDto(),
)


@Serializable
class BlogVotesDto {
    var voteIndex: Byte = 0
    var voteVec: Array<BlogVoteDto> = emptyArray()
}

@Serializable
class BlogVoteDto {
    var index: Byte = 0
    var votes: Int = 0
}

class BlogPart2Dto {
    var distance: Long = 0 //距离,米表示
    var id: Long = 0 //博文id
    var userId: Int = 0//用户id
    var albumId: Long = 0 //专辑id
    var musicId: Long = 0//音乐id
    var agrees: UInt = 0u //点赞数
    var comments: UInt = 0u //评论数
    var ats: UInt = 0u //艾特数
    var collects: UInt = 0u //收藏数
    var visible: Byte = 0 //可见性
    var openComment: Byte = 0 //是否开放评论
    var kind: Byte = 0 //博文类型
//    var musicKind: Byte = 0 //音乐类型
    var status: Byte = 0 //状态
    var vote: Byte = 0//投票
    var cover: String = "" //封面
    var longitude: Double = 0.0 //经度
    var latitude: Double = 0.0//纬度
    var ipTerritory: String = ""//ip属地
    var location: String = "" //本地位置
    var adCode: String = "" // 邮政编号
    var content: String = ""//文字内容
    var labels: IntArray = EmptyArray.int //标签
    var tags: Array<TagsDto> = emptyArray() //用户标签
    var resource: ResourceDto = ResourceDto() //资源
    var coCreates: String = "" //共创
    var createTime: Long = 0 //发布时间
    var coCreateInfos: Array<UserBaseDto> = emptyArray()//共创用户信息
    var blogVote: BlogVotesDto = BlogVotesDto() //投票
    var nickname: String = "" //用户名
    var profile: String = "" //头像
    var gender: Byte = 0 //性别
    var hasPraise: Boolean = false//是否点赞
    var hasCollect: Boolean = false //是否收藏
    var hasAt: Boolean = false //是否at
    var rel: Int = 0 //正向关系
    var revRel: Int = 0//反向关系
    var alias: String = "" //别名
    var atCount: Int = 0 //被at数
    var exposure: Int = 0//爆光度
    var musicInfo: MusicPart1? = null //音乐信息
}

@Serializable
class BlogPart3Dto {
    var hot: Long = 0 //热度
    var id: Long = 0 //博文id
    var userId: Int = 0//用户id
    var albumId: Long = 0 //专辑id
    var musicId: Long = 0//音乐id
    var agrees: UInt = 0u //点赞数
    var comments: UInt = 0u //评论数
    var ats: UInt = 0u //艾特数
    var collects: UInt = 0u //收藏数
    var visible: Byte = 0 //可见性
    var openComment: Byte = 0 //是否开放评论
    var kind: Byte = 0 //博文类型
//    var musicKind: Byte = 0 //音乐类型
    var status: Byte = 0 //状态
    var vote: Byte = 0//投票
    var cover: String = "" //封面
    var longitude: Double = 0.0 //经度
    var latitude: Double = 0.0//纬度
    var ipTerritory: String = ""//ip属地
    var location: String = "" //本地位置
    var adCode: String = "" // 邮政编号
    var content: String = ""//文字内容
    var labels: IntArray = EmptyArray.int //标签
    var tags: Array<TagsDto> = emptyArray() //用户标签
    var resource: ResourceDto = ResourceDto() //资源
    var coCreates: String = "" //共创
    var createTime: Long = 0 //发布时间
    var coCreateInfos: Array<UserBaseDto> = emptyArray()//共创用户信息
    var blogVote: BlogVotesDto = BlogVotesDto() //投票
    var nickname: String = "" //用户名
    var profile: String = "" //头像
    var gender: Byte = 0 //性别
    var hasPraise: Boolean = false//是否点赞
    var hasCollect: Boolean = false //是否收藏
    var hasAt: Boolean = false //是否at
    var rel: Int = 0 //正向关系
    var revRel: Int = 0//反向关系
    var alias: String = "" //别名
    var atCount: Int = 0 //被at数
    var exposure: Int = 0//爆光度
    var musicInfo: MusicPart1? = null //音乐信息
}

@Serializable
class RankingDto {
    var v0: Byte = 0
    var v1: Array<BlogPartDto> = emptyArray()
}



