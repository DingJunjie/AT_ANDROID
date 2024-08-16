package com.bitat.ui.blog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
//import com.bitat.core.ui.components.videoplayer.WeVideoPlayer
//import com.bitat.core.ui.components.videoplayer.rememberVideoPlayerState
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.repository.consts.BLOG_AUDIO_IMAGE
import com.bitat.repository.consts.BLOG_AUDIO_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_AUDIO_ONLY
import com.bitat.repository.consts.BLOG_AUDIO_TEXT
import com.bitat.repository.consts.BLOG_IMAGES_ONLY
import com.bitat.repository.consts.BLOG_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_RICH_TEXT
import com.bitat.repository.consts.BLOG_TEXT_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_IMAGE
import com.bitat.repository.consts.BLOG_VIDEO_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.repository.consts.ESSAY
import com.bitat.repository.consts.NOVEL
import com.bitat.repository.consts.PODCASTS
import com.bitat.repository.consts.POETRY
import com.bitat.repository.consts.RUSTIC
import com.bitat.repository.consts.VIRTUAL
import com.bitat.style.FontStyle
import com.bitat.ui.component.BlogImages
import com.bitat.ui.component.BlogVideo
import com.bitat.ui.theme.grey5


/****
 * // 博文类型
 * const val BLOG_TEXT_ONLY = 1  // 纯文本
 * const val BLOG_IMAGES_ONLY = 2  // 纯图片
 * const val BLOG_VIDEO_ONLY = 4  // 纯视频
 * const val BLOG_AUDIO_ONLY = 8  // 纯音频
 * const val BLOG_IMAGE_TEXT = 3  // 图文
 * const val BLOG_VIDEO_TEXT = 5  // 视文
 * const val BLOG_AUDIO_TEXT = 9  // 音文
 * const val BLOG_VIDEO_IMAGE = 6  // 视图
 * const val BLOG_AUDIO_IMAGE = 10 // 音图
 * const val BLOG_VIDEO_IMAGE_TEXT = 7  // 视图文
 * const val BLOG_AUDIO_IMAGE_TEXT = 11 // 音图文
 * const val BLOG_RICH_TEXT = -1 // 副文本
 * const val PODCASTS = -2 // 播客
 * const val RUSTIC = -3 // 美丽乡村
 * const val VIRTUAL = -4 // 投影
 * const val POETRY = -5 // 诗词
 * const val ESSAY = -6 // 文章
 * const val NOVEL = -7 // 小说
 */

@Composable
fun BlogContent(
    kind: Int,
    mBlogBaseDto: BlogBaseDto,
    maxHeight: Int,
    isPlaying: Boolean = false,
    navHostController: NavHostController
) {
    when (kind) {
        BLOG_TEXT_ONLY -> BlogTextOnlyShow(mBlogBaseDto)
        BLOG_VIDEO_ONLY, BLOG_VIDEO_TEXT -> BlogVideo(
            mBlogBaseDto,
            maxHeight,
            isPlaying,
            navHostController
        )

        BLOG_AUDIO_ONLY -> BlogAudioOnlyShow(mBlogBaseDto)
        BLOG_IMAGE_TEXT, BLOG_IMAGES_ONLY -> BlogImages(mBlogBaseDto, maxHeight)
        BLOG_AUDIO_TEXT -> BlogAudioTextShow(mBlogBaseDto)
        BLOG_VIDEO_IMAGE -> BlogVideoImageShow(mBlogBaseDto)
        BLOG_AUDIO_IMAGE -> BlogAudioImageShow(mBlogBaseDto)
        BLOG_AUDIO_IMAGE_TEXT -> BlogAudioImageTextShow(mBlogBaseDto)
        BLOG_VIDEO_IMAGE_TEXT -> BlogVideoImageTextShow(mBlogBaseDto)
        BLOG_RICH_TEXT -> BlogRichTextShow(mBlogBaseDto)
        PODCASTS -> PodcastsShow(mBlogBaseDto)
        RUSTIC -> RusticShow(mBlogBaseDto)
        VIRTUAL -> VirtualShow(mBlogBaseDto)
        POETRY -> PoetryShow(mBlogBaseDto)
        ESSAY -> EssayShow(mBlogBaseDto)
        NOVEL -> NovelShow(mBlogBaseDto)
    }
}

@Composable
fun NovelShow(mBlogBaseDto: BlogBaseDto) {
    TODO("Not yet implemented")
}

@Composable
fun EssayShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun PoetryShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun VirtualShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun RusticShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun PodcastsShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogRichTextShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogVideoImageTextShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogAudioImageTextShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogAudioImageShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogVideoImageShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogAudioTextShow(mBlogBaseDto: BlogBaseDto) {

}

/***
 * val BLOG_VIDEO_TEXT = 5  // 视文
 */
@Composable
fun BlogVideoTextShow(mBlogBaseDto: BlogBaseDto, maxHeight: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .height(400.dp)
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,

                    ) {

                    Text(
                        text = mBlogBaseDto.content,
                        letterSpacing = 1.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(start = 1.dp),
                        fontSize = FontStyle.contentLargeSize
                    )
                    //视屏

                    val videopath1 = mBlogBaseDto.resource.video
                    //val videopath = "https://media6.smartstudy.com/ae/07/3997/2/dest.m3u8"
                    VideoItem(videoUrl = videopath1)

                }

                Row(
                    modifier = Modifier
                        .width(50.dp)
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var maxHeight_ = maxHeight + 400
                    Canvas(
                        modifier = Modifier
                            .height(maxHeight_.dp)
                            .width(2.dp)
                    ) {
                        drawLine(
                            color = grey5,
                            start = Offset(size.width / 2f, 1f),
                            end = Offset(size.width / 2f, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

            }


        }


    }


}


@Composable
private fun VideoItem(videoUrl: String) {
    Column {
//        val state = rememberVideoPlayerState(videoSource = Uri.parse(videoUrl))
//        WeVideoPlayer(state, modifier = Modifier.fillMaxWidth().fillMaxHeight())

//        Box {
//            var commentsVisible by remember { mutableStateOf(false) }
////            InformationBar(video, onCommentsClick = {
////                commentsVisible = true
////            })
//            CommentList(commentsVisible, video) {
//                commentsVisible = false
//            }
//        }
    }
}

/****
 * val BLOG_IMAGE_TEXT = 3  // 图文
 */
//@Composable
//fun BlogImageTextShow(mBlogBaseDto: BlogBaseDto, maxHeight: Int) {
//    Box(
//        Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    ) {
//        Row(
//            Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight()
//            ) {
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                        .padding(start = 50.dp),
//                    horizontalAlignment = Alignment.Start,
//
//                    ) {
//
//                    Text(
//                        text = mBlogBaseDto.content,
//                        letterSpacing = 1.sp,
//                        color = Color.Black,
//                        textAlign = TextAlign.Left,
//                        modifier = Modifier.padding(start = 1.dp),
//                        fontSize = FontStyle.contentLargeSize
//                    )
//                    LazyRowImg(mBlogBaseDto, maxHeight);
//
//                }
//
//                Row(
//                    modifier = Modifier
//                        .width(50.dp)
//                        .padding(5.dp),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    val lineHeight = maxHeight + 30
//                    Canvas(
//                        modifier = Modifier
//                            .height(lineHeight.dp)
//                            .width(2.dp)
//                    ) {
//                        drawLine(
//                            color = grey5,
//                            start = Offset(size.width / 2f, 1f),
//                            end = Offset(size.width / 2f, size.height),
//                            strokeWidth = 2.dp.toPx()
//                        )
//                    }
//                }
//
//            }
//
//
//        }
//
//
//    }
//
//}


@Composable
fun TotalIndexShow(index: String, size: String) {
    Row {
        Text(
            text = index, color = Color.White
        )
        Text(text = "/", color = Color.White)
        Text(text = size, color = Color.White)
    }

}


@Composable
fun BlogAudioOnlyShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogVideoOnlyShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogImagesOnlyShow(mBlogBaseDto: BlogBaseDto) {

}

@Composable
fun BlogTextOnlyShow(mBlogBaseDto: BlogBaseDto) {

}








