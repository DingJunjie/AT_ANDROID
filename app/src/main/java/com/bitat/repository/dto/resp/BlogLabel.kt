package  com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable

@Serializable
class BlogLabelDto {
    var id: Long = 0 //labelId
    var cover: String = "" //封面
    var name: String = ""//名字
}
    