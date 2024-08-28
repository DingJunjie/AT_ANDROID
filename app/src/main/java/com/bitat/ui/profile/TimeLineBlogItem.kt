package com.bitat.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.bitat.router.NavigationItem
import com.bitat.state.OthersState
import com.bitat.ui.blog.BlogContent
import com.bitat.ui.blog.BlogMorePop
import com.bitat.ui.common.CollapseText
import com.bitat.ui.common.LottieBox
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.UserInfo
import com.bitat.ui.theme.line
import com.bitat.utils.ImageUtils
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.OthersViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*****
 * blog item 组件L
 */
@Composable
fun TimeLineBlogItem(
    blog: BlogBaseDto,
    isPlaying: Boolean = false,
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider,
    contentClick: (BlogBaseDto) -> Unit,
    tapComment: () -> Unit,
    tapAt: () -> Unit,
    tapLike: () -> Unit,
    tapCollect: (Int) -> Unit,
    moreClick: () -> Unit
) {
    println("current blog is ${Json.encodeToString(BlogBaseDto.serializer(), blog)}")
    val height = getHeight(blog)
    val lineHeight = remember {
        mutableIntStateOf(0)
    }

    val unfoldHeight = remember { mutableIntStateOf(0) }
    val isCollapse = remember { mutableStateOf(false) }
    val isMoreVisible = remember {
        mutableStateOf(false)
    }


    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val state by vm.blogState.collectAsState()

    val othersVm: OthersViewModel = viewModelProvider[OthersViewModel::class]

    Column(
        modifier = Modifier //        .onSizeChanged { size ->
            //            if (lineHeight.intValue == 0) {
            //                CuLog.debug(CuTag.Profile, "item size change $size")
            //                lineHeight.intValue = (size.height / Density).toInt()
            //            }
            //
            //        }
            .fillMaxWidth()
    ) { //头像 和用户 和发布时间
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Spacer(modifier = Modifier.height(30.cdp))
            if (!TimeUtils.isThisYear(blog.createTime)) {
                Text(
                    text = TimeUtils.getYearFromString(blog.createTime),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

            Text(
                text = TimeUtils.timeToMD(blog.createTime),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(modifier = Modifier
                .onSizeChanged { size ->
                    if (unfoldHeight.intValue == 0 && isCollapse.value) {
                        unfoldHeight.intValue = (size.height / Density).toInt()
                    }
                    if (lineHeight.intValue == 0 && !isCollapse.value) {
                        lineHeight.intValue = (size.height / Density).toInt()
                    }


                }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.05f) //                    .background(Color.Blue)
                        .fillMaxHeight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Line(if (isCollapse.value) unfoldHeight.intValue else lineHeight.intValue)
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                ) {
                    if (blog.content.isNotEmpty()) {
                        Surface(modifier = Modifier
                            .padding(
                                start = ScreenUtils.screenWidth.times(
                                    0.05
                                ).dp, bottom = 30.cdp
                            )
                            .clickable(indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                contentClick(blog)
                            }) { //                        BlogText(blog.content)
                            CollapseText(
                                value = blog.content,
                                2,
                                modifier = Modifier.fillMaxWidth()
                            ) { res ->
                                isCollapse.value = res
                            }
                        }
                    }

                    //博文类型
                    Box(
                        modifier = Modifier
                            .fillMaxSize() //                        .padding(start = 20.cdp)
                            .background(Color.Transparent)
                    ) {
                        BlogContent(
                            blog.kind.toInt(),
                            blog,
                            height,
                            true,
                            isPlaying,
                            coverIsFull = true,
                            navHostController,
                            viewModelProvider
                        )
                    }

                    Surface(
                        modifier = Modifier.padding(
                            start = ScreenUtils.screenWidth.times(0.05).dp,
                            top = 25.cdp
                        )
                    ) {
                        BlogOperation(blog, tapComment, tapAt, tapLike, tapCollect)
                    }
                    if (state.flag < 0) {
                        Text("")

                    }
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
        modifier = Modifier
            .width(10.dp)
            .fillMaxHeight()
            .height(lineHeight.dp), //            .padding(5.dp)
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(
            modifier = Modifier //                .height(lineHeight.dp)
                .width(2.dp)
                .height(lineHeight.dp)
        ) {
            drawLine(
                color = line,
                start = Offset(size.width / 2f, 1f),
                end = Offset(size.width / 2f, size.height),
                strokeWidth = 1.dp.toPx()
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
    CuLog.debug(CuTag.Blog, "图片高度计算：$height")
    return height
}