package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class CreateUserStateDto(
    var stateType: Byte, // 申诉类型,详情见常量
    var stateReason: String, //申诉理由
    var stateAnnex: Array<String> //申诉附件
)