package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class SmsCaptchaDto(var phone: String = "")

//手机号
@Serializable
class PhoneLoginDto {
    var captcha: String = "" //验证码
    var phone: String = "" //手机号
    var atInfo: String = "" //atToken
    var openId: String = "" //
    var platform: Byte = 0
}

@Serializable
class OneClickLoginDto{
    var token: String =""
    var access: String = ""
    var platform: Byte = 0
}


@Serializable
class ThirdWeChatDto(val code: String)

@Serializable
class ThirdQQDto(val access: String, val openId: String)

@Serializable
class PasswordLoginDto(val captcha: String, //验证码
    val checkKey: String, val password: String, //密码
    val phone: String //手机号
)

@Serializable
class RefreshDto(val token: String, val label: String)

@Serializable
class CancelDto(val captcha: String, val phone: String)