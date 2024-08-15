package com.bitat.ui.blog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.bitat.R
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.ext.cdp
import com.bitat.ext.toPx
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.BlogText
import com.bitat.ui.component.UserInfo
import com.bitat.ui.theme.grey5
import com.bitat.utils.ImageUtils
import com.bitat.utils.ScreenUtils

/*****
 * blog item 组件
 */
@Composable
fun BlogItem(
    blog: BlogBaseDto,
    currentId: Long,
    isCurrent: (Long) -> Unit,
    isPlaying: Boolean = false
) {
    val height = getHeight(blog)
    val lineHeight = remember {
        mutableIntStateOf(height)
    }
    val oneThirdScreenHeight = ScreenUtils.screenHeight.dp.toPx / 3

    Column(modifier = Modifier
//        .onGloballyPositioned { coordinates ->
//            if ((blog.kind.toInt() == BLOG_VIDEO_ONLY || blog.kind.toInt() == BLOG_VIDEO_TEXT)
//                && currentId != blog.id
//            ) {
//                if (coordinates.positionInRoot().y.dp.toPx > oneThirdScreenHeight && coordinates.positionInRoot().y.dp.toPx < oneThirdScreenHeight * 2) isCurrent(
//                    blog.id
//                )
//            }
//        }
        .fillMaxWidth()) { //头像 和用户 和发布时间
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp)
                .padding(start = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            if (blog.profile.isNotEmpty()) {
                CircleImage(blog.profile)
            } else {
                CircleImage(blog.profile)
            }

            Surface(modifier = Modifier.padding(start = 14.dp)) {
                UserInfo(blog.nickname)
            }
//            LottieBox(lottieRes = R.raw.follow_ani,
//                isRepeat = true,
//                modifier = Modifier.size(100.dp))
        }

        CuLog.debug(CuTag.Blog, "博文类型>>>>>>>>>>>>>>" + blog.kind.toInt())

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .width(ScreenUtils.screenWidth.times(0.12).dp)
                    .fillMaxHeight() //                    .background(red1)
            ) { //                Box(
                //                    modifier = Modifier
                //                        .height(lineHeight.intValue.dp)
                //                        .width(2.dp)
                //                        .background(Color.Red)
                //                )
                Line(lineHeight.intValue)
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                if (blog.content.isNotEmpty()) {
                    Surface(modifier = Modifier
                        .onSizeChanged { size -> //                        if (lineHeight.intValue == height) {
                            //                            lineHeight.intValue = with(density) {
                            //                                height + size.height.toDp().toInt()
                            //                            }
                            //                        }
                        }
                        .padding(start = ScreenUtils.screenWidth.times(0.12).dp)) {
                        BlogText(blog.content)
                    }
                }

                //博文类型
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 10.dp)
                        .background(Color.Transparent)
                ) {
                    BlogContent(blog.kind.toInt(), blog, height, currentId, isPlaying)
                }

                Surface(modifier = Modifier.padding(start = ScreenUtils.screenWidth.times(0.12).dp)) {
                    BlogOperation(blog)
                }

            }
        }

        //        Row(
        //            modifier = Modifier
        //                .fillMaxWidth()
        //                .fillMaxHeight()
        //                .background(Color.Black),
        //            horizontalArrangement = Arrangement.Start
        //        ) {
        //            //评论
        //            Common(blog)
        //        }
    }

}

@Composable
fun Line(lineHeight: Int) {
    Row(
        modifier = Modifier
            .width(50.dp)
            .height(lineHeight.dp)
            .padding(5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(
            modifier = Modifier //                .height(lineHeight.dp)
                .width(2.dp)
                .height(lineHeight.dp)
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

fun getHeight(item: BlogBaseDto): Int {
    var height = 0 // 计算图片的高度
    if (item.cover.isNotEmpty() || item.resource.images.isNotEmpty()) {
        val firstImageUrl = item.cover.ifEmpty { item.resource.images.first() } // 获得屏幕的宽度
        val imageMaxWidth = (ScreenUtils.screenWidth * 0.88).toInt()
        val resSize = ImageUtils.getParamsFromUrl(firstImageUrl)
        height = ImageUtils.getHeight(resSize, imageMaxWidth)
    }
    return height
}