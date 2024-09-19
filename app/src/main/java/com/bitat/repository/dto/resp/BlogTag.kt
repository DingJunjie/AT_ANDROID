package  com.bitat.repository.dto.resp

import com.bitat.repository.dto.common.TagsDto
import kotlinx.serialization.Serializable

@Serializable
class BlogTagDto {
    var id: Long = 0 //tagId
    var name: String = "" //名字
    var useNum: Long = 0 //使用数
    var createTime: Long = 0 //创建时间
}
fun BlogTagDto.toBlogTagDto(): TagsDto {
    val tagsDto = TagsDto()
    tagsDto.v0 = this.id
    tagsDto.v1 = this.name
    return tagsDto
}