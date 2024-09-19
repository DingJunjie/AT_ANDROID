package  com.bitat.repository.dto.resp

import com.bitat.repository.dto.common.TopBlogsDto
import kotlinx.serialization.Serializable

@Serializable
class UserDto {
    var id: Long = 0 //用户id
    var phone: String = "" //手机号
    var nickname: String = "" //用户名
    var profile: String = "" //头像
    var cover: String = "" //封面
    var address: String = "" // 城市
    var ip: String = "" //ip
    var introduce: String = "" //用户简介
    var extraInfo: String = "" //其他信息
    var account: String = "" //账号
    var gender: Int = 0 //性别
    var verified: Int = 0 //是否官方认证
    var score: Int = 0 //用户评分
    var friends: Int = 0 //朋友数量
    var fans: Int = 0 //粉丝数
    var follows: Int = 0 //关注数
    var blogs: Int = 0 //动态数
    var photos: Int = 0 //作品数
    var collects: Int = 0 //收藏数
    var agrees: Int = 0 //获赞数
    var ats: Int = 0 //被at数
    var topBlogs: Array<TopBlogsDto> = emptyArray() //专辑置顶
    var inviterId: Long = 0  //邀请者id
    var birthday: Long = 0 //生日
    var loginTime: Long = 0 //上次登录时间
    var createTime: Long = 0 //注册时间
}

@Serializable
class UserBaseDto {
    var id: Long = 0
    var nickname: String = ""
    var profile: String = ""
    var gender: Byte = 0
}

@Serializable
class UserBase3Dto {
    var id: Long = 0
    var nickname: String = ""
    var profile: String = ""
    var gender: Byte = 0
    var alias: String = ""
    var rel: Int = 0
}

@Serializable
data class UserBase1Dto(
    val id: Long = 0L,
    val nickname: String = "",
    val profile: String = "",
    val fans: Int = 0,
    val blogs: UInt = 0u,
    val agrees: UInt = 0u,
    val address: String = "",
    val birthday: Long = 0L,
    val follows: Int = 0,
    val ats: Int = 0,
    val account: String = "",
    val alias: String = "",
    var rel: Int = 0,
    val revRel: Int = 0,
    val gender: Byte = 0,
    val followTime: Long = 0L
)
fun UserBase1Dto.toUserHomeDto():UserHomeDto{
    val userHomeDto=UserHomeDto()
    userHomeDto.id=this.id
    userHomeDto.nickname=this.nickname
    userHomeDto.profile=this.profile
    userHomeDto.fans=this.fans
    userHomeDto.blogs=this.blogs.toInt()
    userHomeDto.agrees=this.agrees.toInt()
    userHomeDto.address=this.address
    userHomeDto.birthday=this.birthday
    userHomeDto.follows=this.follows
    userHomeDto.account=this.account
    userHomeDto.alias=this.alias
    userHomeDto.rel=this.rel
    userHomeDto.revRel=this.revRel
    userHomeDto.gender=this.gender
   return userHomeDto
}

@Serializable
data class UserBase2Dto(
    val id: Long = 0L,
    val nickname: String = "",
    val profile: String = "",
    val fans: Int = 0,
    val follows: Int = 0,
    val account: String = "",
    val alias: String = "",
    val rel: Int = 0,
    val revRel: Int = 0,
    val time: Long = 0L
)



@Serializable
class UserPartDto {
    var id: Long = 0
    var nickname: String = ""
    var profile: String = ""
    var cover: String = ""
    var address: String = ""
    var introduce: String = ""
    var otherJson: String = ""
    var account: String = ""
    var gender: Byte = 0
    var verified: Byte = 0
    var lifeStatus: Byte = 0
    var friends: Int = 0
    var fans: Int = 0
    var follows: Int = 0
    var blogs: Int = 0
    var photos: Int = 0
    var topBlogs: Array<TopBlogsDto> = emptyArray() //专辑置顶
    var collects: Int = 0
    var agrees: Int = 0
    var birthday: Long = 0
    var alias: String = ""
    var rel: Int = 0
    var revRel: Int = 0
}

@Serializable
class UserHomeDto {
    var id: Long = 0
    var nickname: String = ""
    var profile: String = ""
    var cover: String = ""
    var address: String = ""
    var introduce: String = ""
    var otherJson: String = ""
    var account: String = ""
    var gender: Byte = 0
    var verified: Byte = 0
    var lifeStatus: Byte = 0
    var friends: Int = 0
    var fans: Int = 0
    var follows: Int = 0
    var blogs: Int = 0
    var photos: Int = 0
    var topBlogs: Array<TopBlogsDto> = emptyArray() //专辑置顶
    var collects: Int = 0
    var agrees: Int = 0
    var birthday: Long = 0
    var homeOpen:Byte = 0 //主页
    var blogOpen:Byte = 0 //时间线
    var opusOpen:Byte = 0 //作品
    var alias: String = ""
    var rel: Int = 0
    var revRel: Int = 0
}

