package  com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable

@Serializable
class CollectPartDto {
    val key: Int = 0 //收藏key
    val name: String = "" //名字
    val createTime: Long = 0 //创建
    val cover: String = "" //收藏封面
}