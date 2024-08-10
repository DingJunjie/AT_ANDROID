package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class UpdateProfileDto(
    var key: String //头像key
)

@Serializable
class UpdateCoverDto(
    var cover: String //封面key
)

@Serializable
class UpdateNicknameDto(
    var nickname: String //昵称
)

@Serializable
class UpdateIntroduceDto(
    var introduce: String //简介
)

@Serializable
class UserInfoDto(
    var userId: Long //用户id
)

@Serializable
class UpdateUserInfoDto(
    var gender: Byte, // 性别
    var birthday: Long, //生日,毫秒时间戳
    var address: String, //城市
    var extraInfo: String //其他信息
)

@Serializable
class FindBaseByIdsDto(
    var userIds: LongArray //用户id数组
)

@Serializable
class PhotoBlogListDto(
    var pageSize: Int, //第几页
    var lastTime: Long = 0, //最后一条创建时间
    var userId: Long = 0 //用户id
)


@Serializable
class UpdateVisibleDto(
    var kind: Byte, //可见性kind ,详情见常量
    var visibilityOpen: Byte //需要需改的类型,详情见常量
)

@Serializable
class SetAliasDto(
    var userId: Long, //用户id
    var alias: String //别名
)

@Serializable
class UpdatePhoneDto(
    var oldPhone: String, //旧手机号
    var oldCaptcha: String, //旧验证码
    var newPhone: String, //新手机号
    var newCaptcha: String, //新验证码
)

@Serializable
class FindPhoneListDto(
    var phoneList: Array<String> //电话号码数组
)

@Serializable
class GetUserInfoDto(
    var userId: Long //用户id
)

@Serializable
class FindFriendListDto(
    var pageSize: Int = 10, //条数
    var pageNo: Int = 0 //最后一条博文时间
)


@Serializable
class FindPrivateDto(
    var pageSize: Int, //条数
    var lastTime: Long = 0 //最后一条博文时间
)


@Serializable
class FriendRelDto(
    var rel: Int, //正向关系
    var revRel: Int, //反向关系
    var userId: Long, //用户id
    var time: Long, //关注时间
    var alias: String, //别名
)

@Serializable
class SetPassWordDto(
    var phone: String,
    var passWord: String,
    var captcha: String,
)