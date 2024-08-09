package  com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable
import  com.bitat.repository.dto.common.TokenDto
import com.bitat.utils.TimeUtils

@Serializable
class AuthDto {
    //auth
    var v0: Int = 0

    //expire
    var v1: Long = 0
    fun isExpired() = v1 != -1L && v1 < TimeUtils.getNow()
}

@Serializable
class LoginResDto {
    var access: TokenDto = TokenDto() //token信息
    var auths: Array<AuthDto> = emptyArray() //权限
    var user: UserDto = UserDto() //用户信息
}

@Serializable
class CaptchaDto {
    var checkKey: String = ""
    var captcha: String = ""
}

@Serializable
class RefreshResDto {
    var access: TokenDto =  TokenDto()
    var auths: Array<AuthDto> = emptyArray()
}

