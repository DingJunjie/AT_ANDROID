package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class SocialDto(
    var ops: Int,//1关注或拉黑 , 0取消关注或者拉黑
    var userId: Long //用户id
)