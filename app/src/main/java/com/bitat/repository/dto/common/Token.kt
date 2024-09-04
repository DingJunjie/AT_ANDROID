package  com.bitat.repository.dto.common

import kotlinx.serialization.Serializable

@Serializable
class TokenDto {
    var token: String = ""
    var label: String = ""//秘钥
    var expire: Long = 0 //过期时间
}
