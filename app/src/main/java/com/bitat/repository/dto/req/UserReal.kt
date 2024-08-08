package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class GetUserRealDto(
    val idCard: String, //身份证号
    val name: String, //名字
    val key: String, // 用户自拍
    val frontImage: String, //身份证正面
    val backImage: String, //身份证反面
    val phone: String, //手机号
    val captcha: String //验证码
)

