package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable
import com.bitat.repository.dto.common.TagsDto

@Serializable
class SearchCommonDto(
    var pageSize:Int = 10,
    var pageNo:Int = 0,
    var keyword: String //搜索关键字
)

@Serializable
class SearchGuessDto(
    var keyword: String //搜索关键字
)

@Serializable
class RecommendSearchDto(
    var pageSize:Int = 10,
    var labels: IntArray //博文label数组
)

@Serializable
class RecommendSearchDetailDto(
    var pageSize:Int = 50,
    var blogId: Long //博文label数组
)

@Serializable
class FindSearchTagDto(
    var pageSize:Int = 10,
    var pageNo:Int = 0,
    var tag:TagsDto,//搜索的tag
    var blogId: Long //博文id
)
