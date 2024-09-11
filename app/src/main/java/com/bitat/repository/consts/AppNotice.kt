package  com.bitat.repository.consts

// 通知状态
const val APP_NOTICE_FOLLOW = 1                 // 关注
const val APP_NOTICE_NOT_FOLLOW = 2             // 取消关注
const val APP_NOTICE_BLOCK = 3                  // 拉黑
const val APP_NOTICE_NOT_BLOCK = 4              // 取消拉黑
const val APP_NOTICE_AGREE_BLOG = 5             // 博文点赞
const val APP_NOTICE_AGREE_COMMENT = 6          // 一级评论点赞
const val APP_NOTICE_AGREE_SUB_COMMENT = 7      // 二级评论点赞
const val APP_NOTICE_REPLY_COMMENT = 8          // 创建一级评论
const val APP_NOTICE_REPLY_SUB_COMMENT = 9      // 创建评论
const val APP_NOTICE_BLOG_CO_CREATE = 10        // 用户共创
const val APP_NOTICE_REAL = 11                  // 用户实名认证
const val APP_NOTICE_ALBUM = 12                 // 跟动态

//const val APP_NOTICE_USER_INFO = 14           // 谁访问了我
const val APP_NOTICE_AT = 100                   // 博文AT
const val APP_NOTICE_COMMENT_AT = 101           // 评论AT
const val APP_NOTICE_SUB_COMMENT_AT = 102       // 回复AT
const val APP_NOTICE_PARTY_AT = 103             // 活动AT


//系统通知
const val SYSTEM_NOTICE   = 129 //审核不通过
const val SYSTEM_PASSED    = 130 //审核已通过
const val UPDATE_AUTHORITY = 131 //权限相关