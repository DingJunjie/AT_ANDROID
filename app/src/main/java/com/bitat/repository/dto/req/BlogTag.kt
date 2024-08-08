package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class BlogTagFindDto(
    var pageNo: Int, //几页
    var pageSize: Int, //条数
    var searchWord: String //搜索关键字
)