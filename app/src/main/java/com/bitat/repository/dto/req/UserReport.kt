package  com.bitat.repository.dto.req

import com.bitat.utils.EmptyArray
import kotlinx.serialization.Serializable

@Serializable
class CreateUserReportDto(
    var kind: Byte, //举报分类
    var sourceId: Long , //用户举报类型对应的id, 举报用户传userId 、举报博文传博文Id  详情见常量
    var images: Array<String> = arrayOf(""), //图片资源
    var reason: IntArray = EmptyArray.int, // 举报理由
    var remark: String = "" //其他类型,用过用户输入
 )