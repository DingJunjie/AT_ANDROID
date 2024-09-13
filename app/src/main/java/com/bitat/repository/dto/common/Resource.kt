package  com.bitat.repository.dto.common

import kotlinx.serialization.Serializable

//博文资源类
@Serializable
class ResourceDto {
    var images: Array<String> = emptyArray() //图片
    var video: String = "" //视频
    var richText: String = "" //富文本
}
