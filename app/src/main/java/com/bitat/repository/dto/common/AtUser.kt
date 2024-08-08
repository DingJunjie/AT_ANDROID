package  com.bitat.repository.dto.common
import kotlinx.serialization.Serializable

//用户at信息通用类
@Serializable
class AtUserDto(
    var userId: Long, //用户id
    var nickname: String) //用户昵称

