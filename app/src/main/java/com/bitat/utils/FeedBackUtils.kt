package com.bitat.utils

import com.bitat.repository.consts.FEEDBACK_KIND_ACCOUNT
import com.bitat.repository.consts.FEEDBACK_KIND_BLOG
import com.bitat.repository.consts.FEEDBACK_KIND_HOT
import com.bitat.repository.consts.FEEDBACK_KIND_OTHER
import com.bitat.repository.consts.FEEDBACK_KIND_RIGHTS
import com.bitat.repository.consts.FEEDBACK_KIND_SOCIAL

object FeedBackUtils {
    fun getFeedBackTypeList(): Array<ConstBean> {
        val list: Array<ConstBean> = arrayOf(
            ConstBean("账号相关", FEEDBACK_KIND_ACCOUNT, false),
            ConstBean("作品相关", FEEDBACK_KIND_BLOG, false),
            ConstBean("维权举报", FEEDBACK_KIND_RIGHTS, false),
            ConstBean("上热门相关", FEEDBACK_KIND_HOT, false),
            ConstBean("社交互动", FEEDBACK_KIND_SOCIAL, false),
//            ConstBean("钱包相关", FEEDBACK_KIND_WALLET, false),
            ConstBean("其他", FEEDBACK_KIND_OTHER, false),
        )
        return list
    }
   
}
data class ConstBean(
    val name: String = "",
    val type: Int = 0,
    var isSelect: Boolean = false
)