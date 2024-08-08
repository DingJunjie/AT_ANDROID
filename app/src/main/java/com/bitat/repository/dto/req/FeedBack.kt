package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class CreateFeedBackDto(
    var kind: Int, //反馈类型
    var describe: String, //问题描述
    var images: Array<String> //图片文件
)