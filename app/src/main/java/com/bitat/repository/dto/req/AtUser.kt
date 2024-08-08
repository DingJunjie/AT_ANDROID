package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class AtOutUserDto(
    var blogId: Long //博文id
)

@Serializable
class AtGetBlogDto(
    var atInfo: String //token
)

@Serializable
class GetAtUserDto(
    var blogId: Long //博文id
)