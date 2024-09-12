package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.Density
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.state.BlogDetailsType
import com.bitat.ui.common.FollowBtn
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentPopup
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.component.UserInfoWithAddr
import com.bitat.ui.publish.TopBar
import com.bitat.ui.theme.Typography
import com.bitat.viewModel.BlogDetailsViewModel
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import com.bitat.viewModel.TimeLineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun BlogDetailPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val blogListVm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val blogState = blogListVm.blogState.collectAsState()

    val vm: BlogDetailsViewModel = viewModelProvider[BlogDetailsViewModel::class]
    val state = vm.state.collectAsState()
    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val collectState by collectVm.collectState.collectAsState()

    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]

    val timeLineVm: TimeLineViewModel = viewModelProvider[TimeLineViewModel::class]

    val coroutineScope = rememberCoroutineScope()

    val blogDetail = state.value.currentBlog
    var heigh: Int = 0
    state.value.currentBlog?.let {
        heigh = getHeight(it)
    }

    val scrollState = rememberScrollState()
    var isCommentVisible by remember {
        mutableStateOf(false)
    }

    var collectTipY by remember {
        mutableIntStateOf(0)
    }

    var collectTipVisible by remember {
        mutableStateOf(false)
    }

    var collectPopupVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Dispatchers.IO) {
        blogDetail?.let {
            commentVm.updateBlogId(it.id)
            delay(1000)
            isCommentVisible = true
        }
    }
    val toast = rememberToastState()

    BackHandler {
        AtNavigation(navHostController).navigateToHome()
    }

    fun updateList(currentBlog: BlogBaseDto) {
        when (state.value.detailsType) {
            BlogDetailsType.BlogList -> blogListVm.collectClick(currentBlog)
            BlogDetailsType.TimeList -> timeLineVm.collectClick(currentBlog)
            BlogDetailsType.Default -> {}
        }
    }

    Scaffold(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(), topBar = {
        CommonTopBar(
            modifier = Modifier,
            title ="",
            backFn = { AtNavigation(navHostController).navigateToHome() },
            isBg = true,
            paddingStatus = true
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.value.currentBlog?.let {
                Row(
                    modifier = Modifier
                        .height(80.dp)
                        .padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Box(modifier = Modifier.weight(3f)) {
                        UserInfoWithAddr(
                            nickname = it.nickname,
                            createTime = it.createTime,
                            isShowTime = true,
                            avatar = it.profile,
                            address = it.ipTerritory
                        )
                    }
                    if (it.userId != UserStore.userInfo.id)

                        FollowBtn(modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                            rel = it.rel,
                            it.userId,
                            clickFn = { rel ->
                                it.rel = rel // 刷新 blog列表数据
                                blogListVm.refreshCurrent(it)
                            })
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Text(
                        text = it.content,
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = TextStyle(lineHeight = MaterialTheme.typography.bodyMedium.lineHeight)
                    )
                    BlogContent(
                        it.kind.toInt(),
                        it,
                        heigh,
                        needRoundedCorner = true,
                        isPlaying = true,
                        coverIsFull = true,
                        needStartPadding = false,
                        navHostController,
                        viewModelProvider
                    )
                    Spacer(modifier = Modifier.height(40.cdp))
                    BlogOperation(it, tapComment = {

                    }, tapAt = {}, tapCollect = { index ->
                        collectTipY = index.div(Density).toInt()
                        collectVm.updateBlog(blog = it)
                        collectTipVisible = true

                        if (it.hasCollect) { // 已收藏，取消
                            collectVm.cancelCollect() {
                                vm.collectClick(state.value.currentBlog!!)
                                when (state.value.detailsType) {
                                    BlogDetailsType.BlogList -> blogListVm.collectClick(it)
                                    BlogDetailsType.TimeList -> timeLineVm.collectClick(it)
                                    BlogDetailsType.Default -> {}
                                }
                            }
                        } else { // 未收藏，收藏
                            collectVm.collectBlog(0) {
                                vm.collectClick(state.value.currentBlog!!)
                                updateList(it)
                            }
                        }
                        coroutineScope.launch {
                            delay(3000)
                            collectTipVisible = false
                        }

                    }, tapLike = {
                        vm.likeClick(it)
                        updateList(it)
                    }, updateFlag = state.value.flag)
                    Spacer(modifier = Modifier.height(60.cdp))
                }
                Column(
                    modifier = Modifier
                        .height(70.cdp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(start = 5.dp, end = 5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = "全部评论（${it.comments}）",
                        style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimary)
                    )
                }
                Spacer(modifier = Modifier.height(30.cdp))
                if (isCommentVisible) {
                    CommentPopup(
                        visible = true,
                        blogId = commentState.currentBlogId,
                        commentViewModel = commentVm,
                        coroutineScope = coroutineScope,
                        tapImage = {
                            imagePreviewVm.setImagePreView(arrayOf(it))
                            AtNavigation(navHostController).navigateToImagePreviewPage()
                        },
                        commentState = commentState,
                        onClose = { },
                        isPop = false
                    ) {
                        it.comments += 1u
                        vm.setCurrentBlog(it)
                        updateList(it)
                    }
                }
                if (state.value.flag < 0) {
                    Text(text = "")
                }
            }
        }
        CollectTips(collectTipVisible, y = collectTipY, closeTip = {
            collectTipVisible = false
        }, openPopup = {
            collectTipVisible = false
            collectPopupVisible = true
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
                    state.value.currentBlog?.let { blog ->
                        val newBlog =  vm.collectClick(state.value.currentBlog!!)
                        updateList(blog)
                    }
                })
                collectPopupVisible = false
            },
            onClose = {
                collectPopupVisible = false
            })

    }


}


