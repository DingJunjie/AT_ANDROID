package com.bitat.state

import com.bitat.repository.dto.resp.BlogBaseDto

/**
 *    author : shilu
 *    date   : 2024/9/4  10:32
 *    desc   :
 */
data class BlogDetailsState(val currentBlog: BlogBaseDto? = null, val flag: Int = 0, val detailsType: BlogDetailsType = BlogDetailsType.Default)

enum class BlogDetailsType {
    BlogList, TimeList, Default;

}
