package  com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable
import  com.bitat.repository.dto.common.*
import com.bitat.utils.EmptyArray

@Serializable
class AtUserBaseDto {
    var id: Long = 0 //用户id
    var nickname: String = "" //用户昵称
    var profile: String = "" //用户头像
    var gender: Byte = 0 //用户性别
    var alias: String = "" //别名
    var rel: Int = 0 //关系
    var atCount: Int = 0 //被at数量
    var atAllNum: Int = 0 //总at次数
}

@Serializable
class AtUserPart2Dto {
    var firstAtIds: Array<UserBase3Dto> = emptyArray() //最先at我的人
    var lastAtIds: Array<UserBaseDto> = emptyArray() //最后at我的人
    var maxUsers: Array<AtUserBaseDto> = emptyArray()//at最多的人
}

@Serializable
class BlogAtDto {
    var atUserId: Long = 0//at人id
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