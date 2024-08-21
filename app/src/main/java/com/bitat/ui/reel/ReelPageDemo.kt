package com.bitat.ui.reel

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_IMAGES_ONLY
import com.bitat.repository.consts.BLOG_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.router.AtNavigation
import com.bitat.ui.common.CollapseText
import com.bitat.ui.component.ImageBanner
import com.bitat.ui.component.UserInfoWithAvatar
import com.bitat.ui.publish.Options
import com.bitat.ui.theme.Typography
import com.bitat.viewModel.BlogViewModel


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelPageDemo(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[BlogViewModel::class]
    val state = vm.blogState.collectAsState()
    val pagerState = rememberPagerState(initialPage = state.value.resIndex,
        pageCount = { state.value.resList.size })
    var currentDto = remember { state.value.resList[state.value.resIndex] }
    val horPagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    // 监听当前页面的变化
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> // 页面切换时触发的操作
            CuLog.debug(CuTag.Blog, "1111Current page: $page")
            vm.setResIndex(page)
        }
    }

    val isPlay = mutableStateOf(true)

    VerticalPager(state = pagerState) { page -> // Our page content
        //            Text(text = "Page: $page", modifier = Modifier.fillMaxWidth().height(100.dp))

        currentDto = state.value.resList[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            // 视频/图片 部分
            when (currentDto.kind.toInt()) {
                BLOG_VIDEO_ONLY, BLOG_VIDEO_TEXT -> {
                    if (page == state.value.resIndex) { //                    isPlay.val ue = page == state.value.resIndex
                        CuExoPlayer(
                            data = currentDto.resource.video,
                            modifier = Modifier.fillMaxSize(),
                            cover = currentDto.cover,
                            isFixHeight = true
                        )
                    }
                }

                BLOG_IMAGE_TEXT, BLOG_IMAGES_ONLY -> {
                    ImageBanner(currentDto.resource.images.toList(), true)
                }
            } // 用户信息部分
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .height(130.cdp)
                        .padding(end = 50.dp)
                ) {
                    UserInfoWithAvatar(
                        modifier = Modifier.fillMaxSize(),
                        currentDto.nickname,
                        currentDto.profile,
                        textStyle = Typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left
                        ),
                        isShowMore = false,
                        avatarSize = 40
                    )
                }
                CollapseText(
                    value = currentDto.content,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp),
                    textStyle = Typography.bodyLarge.copy(color = Color.White, lineHeight = 26.sp),
                    maxLength = 17
                )

                if (currentDto.location.isNotEmpty()) Options(title = currentDto.location,
                    iconPath = "svg/location_line.svg",
                    selected = false,
                    modifier = Modifier.height(30.dp),
                    tapFn = {

                    })
            }
        }


    }
}