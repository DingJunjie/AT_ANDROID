package  com.bitat.repository.consts

// 通知状态
const val APP_NOTICE_FOLLOW = 1            // 关注
const val APP_NOTICE_NOT_FOLLOW = 2        // 取消关注
const val APP_NOTICE_BLOCK = 3             // 拉黑
const val APP_NOTICE_NOT_BLOCK = 4         // 取消拉黑
const val APP_NOTICE_AT = 5                // at
const val APP_NOTICE_AGREE_BLOG = 6        // 博文点赞
const val APP_NOTICE_AGREE_COMMENT = 7     // 一级评论点赞
const val APP_NOTICE_AGREE_SUB_COMMENT = 8 // 二级评论点赞
const val APP_NOTICE_REPLY_COMMENT = 9     // 创建一级评论
const val APP_NOTICE_REPLY_SUB_COMMENT = 10// 创建评论
const val APP_NOTICE_BLOG_CO_CREATE = 11   // 用户共创
const val APP_NOTICE_REAL = 12             // 用户实名认证
const val APP_NOTICE_ALBUM = 13            // 跟动态
const val APP_NOTICE_USER_INFO = 14        // 谁访问了我

//系统通知
const val SYSTEM_NOTICE   = 129 //审核不通过
const val SYSTEM_PASSED    = 130 //审核已通过
const val UPDATE_AUTHORITY = 131 //权限相关