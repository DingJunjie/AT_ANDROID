package  com.bitat.repository.consts

//评论可见性
const val BLOG_COMMENTABLE_CLOSE = -1  // 关闭评论
const val BLOG_COMMENTABLE_SELF = 1     // 仅自己评论
const val BLOG_COMMENTABLE_FRIEND = 2  // 仅好友评论
const val BLOG_COMMENTABLE_FENS = 3    // 仅粉丝（或好友）评论
const val BLOG_COMMENTABLE_ALL = 4     // 所有人可评论

//一级评论
const val COMMENT_KIND = 1
//二级评论
const val COMMENT_SUB_KIND = 2

//评论是否点赞
const val COMMENT_AGREE_YES = 1
const val COMMENT_AGREE_NO = -1