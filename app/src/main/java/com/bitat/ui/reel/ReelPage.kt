package com.bitat.ui.reel

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import chaintech.videoplayer.model.PlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerView
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_IMAGES_ONLY
import com.bitat.repository.consts.BLOG_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.router.AtNavigation
import com.bitat.ui.common.CollapseText
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.VideoPreview
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.component.AtButton
import com.bitat.ui.component.CollectButton
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentButton
import com.bitat.ui.component.CommentPopup
import com.bitat.ui.component.ImageBanner
import com.bitat.ui.component.LikeButton
import com.bitat.ui.component.UserInfoWithAvatar
import com.bitat.ui.profile.OthersPage
import com.bitat.ui.theme.Typography
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import com.bitat.viewModel.OthersViewModel
import com.bitat.viewModel.ReelViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelPageDemo(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ReelViewModel::class]
    val state = vm.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = state.value.resIndex,
        pageCount = { state.value.resList.size })
    val horPagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]

    val isCommentVisible = remember {
        mutableStateOf(false)
    }

    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val collectState by collectVm.collectState.collectAsState()

    val blogVm = viewModelProvider[BlogViewModel::class]

    val othersVm: OthersViewModel = viewModelProvider[OthersViewModel::class]


    var collectTipY by remember {
        mutableIntStateOf(0)
    }

    var collectTipVisible by remember {
        mutableStateOf(false)
    }

    var collectPopupVisible by remember {
        mutableStateOf(false)
    }


    val coroutineScope = rememberCoroutineScope()
    val toast = rememberToastState()

    // 监听当前页面的变化
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> // 页面切换时触发的操作
            if (page == pagerState.pageCount - 1) {
                vm.getList(false) {
                    ToastModel("加载更多成功", ToastModel.Type.Success).showToast()
                }
            }
            vm.setIndex(page)
        } //        CuLog.debug(CuTag.Blog, "1111 init data")
    }

    //    LaunchedEffect(Unit) { // 初始化数据
    //        CuLog.debug(CuTag.Blog, "1111 init data")
    //        vm.getList(true)
    //    }

    DisposableEffect(Unit) {
        vm.getList(true) {}
        onDispose { // 页面离开时可以执行一些清理操作
            vm.reset()
        }
    }

    //    BackHandler {
    //        AtNavigation(navController).navigateToHome()
    //    }


    val isPlay = mutableStateOf(true)
    val horpagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    HorizontalPager(state = horpagerState) { horPage ->
        when (horPage) {
            0 -> {
                VerticalPager(state = pagerState) { page -> // Our page content
                    //            Text(text = "Page: $page", modifier = Modifier.fillMaxWidth().height(100.dp))
                    var currentDto = state.value.resList[page]
                    CuLog.debug(CuTag.Blog, "1111 id:${currentDto.id}，agrees:${currentDto.agrees}")
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)) { // 视频/图片 部分
                        when (currentDto.kind.toInt()) {
                            BLOG_VIDEO_ONLY, BLOG_VIDEO_TEXT -> {
                                if (page == state.value.resIndex) { //                    isPlay.val ue = page == state.value.resIndex
                                    othersVm.initUserId(currentDto.userId)
//                                    CuExoPlayer(data = currentDto.resource.video,
//                                        modifier = Modifier.fillMaxSize(),
//                                        cover = currentDto.cover,
//                                        isFixHeight = true)
                                    VideoPlayerView(modifier = Modifier.fillMaxSize(),
                                        url = currentDto.resource.video,
                                        playerConfig = PlayerConfig(
                                            isPauseResumeEnabled = false,
                                            isSeekBarVisible = false,
                                            isDurationVisible = false,
                                            seekBarThumbColor = Color.Red,
                                            seekBarActiveTrackColor = Color.Red,
                                            seekBarInactiveTrackColor = Color.White,
                                            durationTextColor = Color.White,
                                            seekBarBottomPadding = 10.dp,
                                            pauseResumeIconSize = 40.dp,
                                            isAutoHideControlEnabled = true,
                                            controlHideIntervalSeconds = 0,
                                            isFastForwardBackwardEnabled = false
                                        )
                                    )

                                }
                            }

                            BLOG_IMAGE_TEXT, BLOG_IMAGES_ONLY -> {
                                ImageBanner(currentDto.resource.images.toList(), true)
                            }
                        } // 用户信息部分
                        Column(modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 30.cdp, bottom = 50.cdp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Bottom) {
                            Column(modifier = Modifier
                                .height(130.cdp)
                                .padding(end = 50.dp)) {
                                UserInfoWithAvatar(modifier = Modifier.fillMaxSize(),
                                    currentDto.nickname,
                                    currentDto.profile,
                                    textStyle = Typography.bodyLarge.copy(fontSize = 14.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Left),
                                    isShowMore = false,
                                    avatarSize = 40)
                            }
                            CollapseText(value = currentDto.content,
                                maxLines = 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 50.dp),
                                textStyle = Typography.bodyLarge.copy(color = Color.White,
                                    lineHeight = 26.sp),
                                maxLength = 17)
                            Spacer(modifier = Modifier.height(5.dp))

                            //                                if (currentDto.location.isNotEmpty()) Options(title = currentDto.location,
                            //                                    iconPath = "svg/location_line.svg",
                            //                                    selected = false,
                            //                                    modifier = Modifier.height(30.dp),
                            //                                    tapFn = {
                            //
                            //                                    })
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // 点赞、收藏 部分
                        Column(modifier = Modifier
                            .padding(end = 20.cdp, bottom = 50.cdp)
                            .align(Alignment.BottomEnd),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            LikeButton(modifier = Modifier.size(26.dp), id = currentDto.id,
                                count = currentDto.agrees.toInt(),
                                isLiked = currentDto.hasPraise,
                                tintColor = Color.White) { //刷新页面、列表状态
                                vm.likeClick(currentDto)
                                blogVm.refreshCurrent(currentDto)

                            }
                            Spacer(modifier = Modifier.height(70.cdp))
                            AtButton(modifier = Modifier.size(28.dp),
                                count = currentDto.ats.toInt(), tintColor = Color.White) {

                            }
                            Spacer(modifier = Modifier.height(70.cdp))
                            CommentButton(modifier = Modifier.size(25.dp),count = currentDto.comments.toInt(),
                                tintColor = Color.White) {
                                coroutineScope.launch {
                                    commentVm.updateBlogId(currentDto.id)
                                    delay(1000)
                                }
                                isCommentVisible.value = true
                            }
                            Spacer(modifier = Modifier.height(70.cdp))
                            CollectButton(currentDto.hasCollect,
                                tintColor = Color.White,
                                modifier = Modifier
                                    .size(27.dp)
                                    .onGloballyPositioned {
                                        collectTipY = it.positionInWindow().y.toInt()
                                        CuLog.debug(CuTag.Blog, "收藏位置更新 $collectTipY")
                                    }) {

                                collectVm.updateBlog(blog = currentDto)

                                collectTipVisible = true
                                if (currentDto.hasCollect) { // 已收藏，取消
                                    collectVm.cancelCollect()
                                } else { // 未收藏，收藏
                                    collectVm.collectBlog(0)
                                }
                                vm.collectClick(currentDto)
                                coroutineScope.launch {
                                    delay(3000)
                                    collectTipVisible = false
                                }
                            }
                            Spacer(modifier = Modifier.height(160.cdp))

                            SvgIcon(
                                modifier = Modifier.size(40.dp),
                                path = "svg/record_music.svg",
                                tint = Color.Unspecified,
                                contentDescription = "",
                            )
                            Spacer(modifier = Modifier.height(30.dp))

                        }

                        CollectTips(collectTipVisible, y = collectTipY, closeTip = {
                            collectTipVisible = false
                        }, openPopup = {
                            collectTipVisible = false
                            collectPopupVisible = true
                        })

                    }
                }
            }

            1 -> {
                OthersPage(navController = navController, viewModelProvider = viewModelProvider)
            }

        }
    }

    //    Column(modifier = Modifier) { //短视频观看
    //        val urlList = mutableListOf<String>()
    //
    //        state.value.resList.forEachIndexed { index, blogBaseDto ->
    //            urlList.add(blogBaseDto.resource.video)
    //        }
    //
    //        ReelsPlayerView(modifier = Modifier.fillMaxSize(),
    //            urls = urlList,
    //            playerConfig = PlayerConfig(
    //                isSeekBarVisible = false,
    //                isDurationVisible = false,
    //                isAutoHideControlEnabled = false,
    //                isPauseResumeEnabled=true,
    //
    //            )
    //        )
    //    }

    CommentPopup(visible = isCommentVisible.value,
        blogId = commentState.currentBlogId,
        commentViewModel = commentVm,
        coroutineScope = coroutineScope,
        tapImage = {
            imagePreviewVm.setImagePreView(arrayOf(it))
            AtNavigation(navController).navigateToImagePreviewPage()
        },
        commentState = commentState,
        onClose = { isCommentVisible.value = false },
        commentSuccess = { //评论成功回调
            var currentDto = state.value.resList[state.value.resIndex]
            currentDto.comments = currentDto.comments + 1u
            vm.refreshCurrent(currentDto)
        })


    CollectPopup(visible = collectPopupVisible,
        collectViewModel = collectVm,
        collectState = collectState,
        createCollection = {
            collectVm.createCollection(it, completeFn = {
                collectVm.initMyCollections()
            })
        },
        tapCollect = {
            collectVm.collectBlog(it.key, completeFn = {
                toast.show("收藏成功")
            })
            collectPopupVisible = false
        },
        onClose = {
            collectPopupVisible = false
        })
}