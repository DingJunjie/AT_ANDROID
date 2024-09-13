package  com.bitat.repository.dto.common

import kotlinx.serialization.Serializable

//博文资源类
@Serializable
class ResourceDto {
    var video: String = "" //视频
    var images: Array<String> = emptyArray() //图片
}
