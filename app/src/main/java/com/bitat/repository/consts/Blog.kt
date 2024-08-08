package  com.bitat.repository.consts

// 博文类型
const val BLOG_TEXT_ONLY = 1  // 纯文本
const val BLOG_IMAGES_ONLY = 2  // 纯图片
const val BLOG_VIDEO_ONLY = 4  // 纯视频
const val BLOG_AUDIO_ONLY = 8  // 纯音频
const val BLOG_IMAGE_TEXT = 3  // 图文
const val BLOG_VIDEO_TEXT = 5  // 视文
const val BLOG_AUDIO_TEXT = 9  // 音文
const val BLOG_VIDEO_IMAGE = 6  // 视图
const val BLOG_AUDIO_IMAGE = 10 // 音图
const val BLOG_VIDEO_IMAGE_TEXT = 7  // 视图文
const val BLOG_AUDIO_IMAGE_TEXT = 11 // 音图文
const val BLOG_RICH_TEXT = -1 // 副文本
const val PODCASTS = -2 // 播客
const val RUSTIC = -3 // 美丽乡村
const val VIRTUAL = -4 // 投影
const val POETRY = -5 // 诗词
const val ESSAY = -6 // 文章
const val NOVEL = -7 // 小说

//投票
const val BLOG_VOTE_YES = 1 // 表示博文是投票

//博文可见性
const val BLOG_VISIBLE_SELF = 1  // 仅自己可见
const val BLOG_VISIBLE_FRIEND = 2  // 仅好友可见
const val BLOG_VISIBLE_ALL = 3  // 所有人可见


//博文是否点赞
const val BLOG_AGREE_YES = 1
const val BLOG_AGREE_NO = -1
