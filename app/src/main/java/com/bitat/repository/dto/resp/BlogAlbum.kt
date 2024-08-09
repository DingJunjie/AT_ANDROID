package  com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable
import com.bitat.repository.dto.common.TagsDto
import com.bitat.utils.EmptyArray

@Serializable
class BlogAlbumDto {
    var id: Long = 0 //专辑id
    var userId: Long = 0 //用户id
    var open: Byte = 0//跟随状态
    var cover: String = "" //封面
    var tag: Array<TagsDto> = emptyArray() //tag
    var members: IntArray = EmptyArray.int //跟随团队
    var createTime: Long = 0//创建时间
}