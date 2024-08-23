package com.bitat.ui.blog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.ext.Density
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.CollapseText
import com.bitat.ui.common.LottieBox
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.UserInfo
import com.bitat.ui.theme.line
import com.bitat.utils.ImageUtils
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogViewModel

/*****
 * blog item 组件L
 */
@Composable
fun BlogItem(blog: BlogBaseDto, isPlaying: Boolean = false, navHostController: NavHostController, viewModelProvider: ViewModelProvider, contentClick: (BlogBaseDto) -> Unit, tapComment: () -> Unit, tapAt: () -> Unit, tapLike: () -> Unit, tapCollect: (Int) -> Unit, moreClick: () -> Unit) {
    val height = getHeight(blog) //    val height = 500
    val lineHeight = remember {
        mutableIntStateOf(0)
    }
    val isMoreVisible = remember {
        mutableStateOf(false)
    }


    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val state by vm.blogState.collectAsState()

    Column(modifier = Modifier //        .onGloballyPositioned { coordinates ->
        .onSizeChanged { size ->
            if (lineHeight.intValue == 0) {
                lineHeight.intValue = (size.height / Density).toInt() - 75
            }
        }.fillMaxWidth().fillMaxHeight()) { //头像 和用户 和发布时间
        Row(modifier = Modifier.fillMaxWidth().height(88.cdp).padding(start = 5.dp),
            horizontalArrangement = Arrangement.Start) {
            Avatar(blog.profile, size = 35)

            Surface(modifier = Modifier.padding(start = 14.dp)) {
                UserInfo(blog.nickname) {
                    isMoreVisible.value = true
                    moreClick()
                }
            }
        }

        CuLog.debug(CuTag.Blog,
            "博文类型>>>>>>>>>>>>>>" + blog.kind.toInt() + "用户id：${blog.userId},关系：${blog.rel},位置：${blog.ipTerritory},lab:${blog.labels}")

        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(Color.Transparent)) {
            Column(modifier = Modifier.width(ScreenUtils.screenWidth.times(0.12).dp)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally //                    .background(red1)
            ) { //                Box(
                //                    modifier = Modifier
                //                        .height(lineHeight.intValue.dp)
                //                        .width(2.dp)
                //                        .background(Color.Red)
                //                )
                Line(lineHeight.intValue)
                LottieBox(lottieRes = R.raw.follow_ani,
                    isRepeat = true,
                    modifier = Modifier.size(40.cdp))

            }

            Column(horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth().background(Color.Transparent)
                    .padding(start = ScreenUtils.screenWidth.times(0.11).dp)) {
                if (blog.content.isNotEmpty()) {
                    Surface(modifier = Modifier.padding(bottom = 10.dp)
                        .clickable { contentClick(blog) }) { //                        BlogText(blog.content)
                        CollapseText(value = blog.content, 2, modifier = Modifier.fillMaxWidth())
                    }
                }

                //博文类型
                Box(modifier = Modifier.fillMaxSize().padding(end = 10.dp)
                    .background(Color.Transparent)) {
                    BlogContent(blog.kind.toInt(),
                        blog,
                        height,
                        true,
                        isPlaying,
                        coverIsFull = true,
                        navHostController,
                        viewModelProvider)
                }

                Surface(modifier = Modifier.padding()) {
                    BlogOperation(blog, tapComment, tapAt, tapLike, tapCollect)
                }
                if (state.flag < 0) {
                    Text("")

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

        if (isMoreVisible.value) {
            BlogMorePop(isMoreVisible.value, blog, navHostController) {
                isMoreVisible.value = false
            }
        }
    }

}

@Composable
fun Line(lineHeight: Int) {
    Row(
        modifier = Modifier.width(50.dp).height(lineHeight.dp).padding(5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier //                .height(lineHeight.dp)
            .width(2.dp).height(lineHeight.dp)) {
            drawLine(color = line,
                start = Offset(size.width / 2f, 1f),
                end = Offset(size.width / 2f, size.height),
                strokeWidth = 1.dp.toPx())
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