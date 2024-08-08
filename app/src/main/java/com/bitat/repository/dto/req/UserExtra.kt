package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class AddStickerDto(
    var key: String //七牛key
)

@Serializable
class FindStickersDto(
    var userId: Long //用户id
)

@Serializable
class DeleteStickerDto(
    var key: String //七牛key
)

@Serializable
class CreateCollectDto(
    var name: String //收藏夹名称
)

@Serializable
class DeleteCollectDto(
    var key: Int //收藏夹key
)

@Serializable
class UpdateCollectDto(
    var key: Int, //收藏夹key
    var name: String //新名称
)

@Serializable
class FindCollectOpusDto(
    var pageSize: Int, //条数
    var lastTime: Long = 0 //最后一条创建时间
)

@Serializable
class CollectNextListDto(
    var pageSize: Int, //条数
    var pageNo: Int, //页数
    var key: Int = 0
)

@Serializable
class IsApplyDto(
    var ops: Byte // 权限类型,详情看常量
)