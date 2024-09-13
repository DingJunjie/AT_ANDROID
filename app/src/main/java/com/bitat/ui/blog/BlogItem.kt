package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.bitat.ext.Density
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.router.Others
import com.bitat.ui.common.CollapseText
import com.bitat.ui.common.LottieBox
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.UserInfo
import com.bitat.ui.theme.line
import com.bitat.ui.theme.lineColor
import com.bitat.utils.ImageUtils
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogMoreViewModel
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.FollowBtnViewModel
import com.bitat.viewModel.OthersViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.serialization.json.Json

/*****
 * blog item 组件L
 */
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BlogItem(
    blog: BlogBaseDto,
    isPlaying: Boolean = false,
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider,
    contentClick: (BlogBaseDto) -> Unit,
    tapComment: () -> Unit,
    tapAt: () -> Unit,
    tapLike: () -> Unit,
    tapCollect: (Int) -> Unit,
    moreClick: () -> Unit,
    onRemove: () -> Unit
) {
    println("current blog is ${Json.encodeToString(BlogBaseDto.serializer(), blog)}")
    val height = getHeight(blog) //    val height = 500
    val lineHeight = remember {
        mutableIntStateOf(0)
    }
    val isMoreVisible = remember {
        mutableStateOf(false)
    }


    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val state by vm.blogState.collectAsState()

    val othersVm: OthersViewModel = viewModelProvider[OthersViewModel::class]
    val moreVm: BlogMoreViewModel = viewModelProvider[BlogMoreViewModel::class]
    val followVm: FollowBtnViewModel = viewModelProvider[FollowBtnViewModel::class]
//    AnimatedVisibility(
//        visible = true,
//        enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
//        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(),
//        initiallyVisible = false
//    ) {
    Column(modifier = Modifier //        .onGloballyPositioned { coordinates ->
        .onSizeChanged { size ->
            if (lineHeight.intValue == 0) {
                lineHeight.intValue = (size.height / Density).toInt() - 75
            }
        }
        .fillMaxWidth()
        .fillMaxHeight()
    ) { //头像 和用户 和发布时间
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp), //                .padding(start = 5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            Avatar(
                blog.profile,
                size = 35,
                showFollow = blog.rel == DEFAULT && blog.userId != UserStore.userInfo.id,
                tapFn = {
                    if (blog.userId != UserStore.userInfo.id) {
                        vm.setCurrentBlog(blog)
//                        othersVm.initUserId(blog.userId)
//                        navHostController.navigate(NavigationItem.Others.route)
                        navHostController.navigate(Others(otherId = blog.userId))
                    } else {
                        navHostController.navigate(NavigationItem.Profile.route)
                    }
                },
                follow = {
                    followVm.followUser(
                        blog.rel,
                        blog.revRel,
                        blog.userId,
                        onSuccess = { resultRel ->
                            blog.rel = resultRel
                            vm.setCurrentBlog(blog)
                        },
                        onError = { error ->

                            when (error.code) {
                                -1 -> ToastModel(error.msg, ToastModel.Type.Error, 1000).showToast()
                            }
                        })
                })

            Spacer(modifier = Modifier.width(15.cdp))
            Surface(
                modifier = Modifier //                .padding(start = 14.dp)
            ) {
                UserInfo(blog.nickname) {
                    vm.setCurrentBlog(blog)
                    moreVm.setUser(blog.userId)
                    isMoreVisible.value = true
                    moreClick()
                }
            }
        }

        CuLog.debug(
            CuTag.Blog,
            "博文类型>>>>>>>>>>>>>>" + blog.kind.toInt() + "用户id：${blog.userId},关系：${blog.rel},位置：${blog.ipTerritory},lab:${blog.labels}"
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Transparent)) {
            Column(
                modifier = Modifier
                    .width(ScreenUtils.screenWidth.times(0.1).dp) //                    .background(Color.Blue)
                    .fillMaxHeight()
                    .padding(start = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Line(lineHeight.intValue - 10)
                LottieBox(
                    lottieRes = R.raw.follow_ani,
                    isRepeat = true,
                    modifier = Modifier.size(40.cdp)
                )
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                if (blog.content.isNotEmpty()) {
                    Surface(modifier = Modifier
                        .padding(
                            start = 40.dp + 15.cdp,
                            end = 30.cdp,
                            bottom = 30.cdp
                        )
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            contentClick(blog)
                        }) { //                        BlogText(blog.content)
                        CollapseText(value = blog.content, 2, modifier = Modifier.fillMaxWidth())
                    }
                } //博文类型
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)) {
                    BlogContent(
                        blog.kind.toInt(),
                        blog,
                        height,
                        true,
                        isPlaying,
                        coverIsFull = true,
                        needStartPadding = true,
                        navHostController,
                        viewModelProvider
                    )
                }

                Surface(modifier = Modifier.padding(start = 40.dp + 15.cdp)) {
                    BlogOperation(
                        blog = blog,
                        tapComment = tapComment,
                        tapAt = tapAt,
                        tapLike = tapLike,
                        tapCollect = tapCollect,
                        updateFlag = state.flag
                    )
                }
                if (state.flag < 0) {
                    Text("")

                }
            }
        }

        if (isMoreVisible.value) {
            BlogMorePop(isMoreVisible.value, blog, navHostController, viewModelProvider, onClose = {
                isMoreVisible.value = false
            }, onRemove = { onRemove() })
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp))
        Divider(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp), color = lineColor)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(10.dp))
    }
//    }
}

@Composable
fun Line(lineHeight: Int) {
    Row(
        modifier = Modifier
            .width(10.dp)
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
    return height
}