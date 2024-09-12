package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.Density
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.PROFILE_MINE
import com.bitat.repository.consts.PROFILE_OTHER
import com.bitat.router.AtNavigation
import com.bitat.state.BlogDetailsType
import com.bitat.state.BlogOperation
import com.bitat.ui.blog.BlogMorePop
import com.bitat.ui.common.ListFootView
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentPopup
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogDetailsViewModel
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import com.bitat.viewModel.OthersViewModel
import com.bitat.viewModel.ProfileViewModel
import com.bitat.viewModel.TimeLineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/8/27  17:47
 *    desc   :
 */

@Composable
fun TimeLinePage(
    type: Int,
    userId: Long,
    navController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm: TimeLineViewModel = viewModelProvider[TimeLineViewModel::class]
    val state = vm.state.collectAsState()

    val profileVm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val profileState by profileVm.uiState.collectAsState()

    val otherVm: OthersViewModel = viewModelProvider[OthersViewModel::class]
    val otherState by otherVm.othersState.collectAsState()

    val detailsVm: BlogDetailsViewModel = viewModelProvider[BlogDetailsViewModel::class]

    val playingIndex = remember {
        mutableStateOf(0)
    }
    LaunchedEffect(Dispatchers.IO) {
        when (type) {
            PROFILE_MINE -> { //                vm.reset()
                if (state.value.isFirst) {
                    vm.timeLineInit(userId)
                    vm.firstFetchFinish()
                }
            }

            PROFILE_OTHER -> { //                vm.reset()
                if (state.value.isFirst) {
                    vm.timeLineInit(userId)
                    vm.firstFetchFinish()
                }
            }
        }

    }

    fun loadMore() {
        if (profileState.profileType == 0) {
            if (state.value.timeLineList.isNotEmpty()) {
                val lastTime = state.value.timeLineList.last().createTime
                vm.timeLineInit(userId = userId, lastTime = lastTime)
            } else {
                vm.timeLineInit(userId = userId)
            }
        }
    }

    // 加载更多
    LaunchedEffect(profileState.isAtBottom) {
        loadMore()
    }

    // 加载更多
    LaunchedEffect(otherState.isAtBottom) {
        CuLog.debug(CuTag.Profile, "timeLine滑动到底部")
        loadMore()
    }

    DisposableEffect(Unit) {
        onDispose { //重置页面参数
            vm.reset()
        }
    }


    val coroutineScope = rememberCoroutineScope()
    val commentVm: CommentViewModel = viewModel()
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModel()
    val collectState by collectVm.collectState.collectAsState()
    val isCommentVisible = remember {
        mutableStateOf(false)
    }

    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]

    var collectTipY by remember {
        mutableIntStateOf(0)
    }

    var collectTipVisible by remember {
        mutableStateOf(false)
    }

    var collectPopupVisible by remember {
        mutableStateOf(false)
    }
    val toast = rememberToastState()

    val isMoreVisible = remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = ScreenUtils.screenHeight.dp - 56.dp)
                .padding(start = 5.dp)
        ) {

            state.value.timeLineList.forEachIndexed { index, item ->
                TimeLineBlogItem(blog = item,
                    isPlaying = false,
                    navHostController = navController,
                    viewModelProvider = viewModelProvider,
                    tapComment = {
                        coroutineScope.launch {
                            commentVm.updateBlogId(item.id)
                            delay(1000)
                        }
                        isCommentVisible.value = true
                    },
                    tapAt = {},
                    tapLike = { //更新列表中 点赞数据
                        vm.likeClick(item)
                    },
                    tapCollect = {
                        collectTipY = it.div(Density).toInt()
                        collectVm.updateBlog(blog = item)
                        collectTipVisible = true
                        if (item.hasCollect) { // 已收藏，取消
                            collectVm.cancelCollect() {
                                vm.collectClick(item)
                            }

                        } else { // 未收藏，收藏
                            collectVm.collectBlog(0) { vm.collectClick(item) }
                        }

                        coroutineScope.launch {
                            delay(3000)
                            collectTipVisible = false
                        }
                    },
                    contentClick = { item ->
                        detailsVm.pageType(BlogDetailsType.TimeList)
                        detailsVm.setCurrentBlog(item)
                        vm.setCurrentBlog(item)
                        AtNavigation(navController).navigateToBlogDetail()
                    },
                    moreClick = {
                        vm.setCurrentBlog(item)
                        isMoreVisible.value = true
                    })
            }

            ListFootView(state.value.isLoadMore, state.value.loadResp) {
                loadMore()
            }
        }
        CollectTips(collectTipVisible, y = collectTipY, closeTip = {
            collectTipVisible = false
        }, openPopup = {
            collectTipVisible = false
            collectPopupVisible = true
        })
    }


    state.value.currentBlog?.let {
        BlogMorePop(isMoreVisible.value, it, navController, viewModelProvider, onClose = {
            isMoreVisible.value = false
        }, onRemove = {
            vm.removeOne(it)
        })
    }

    CommentPopup(visible = isCommentVisible.value,
        blogId = commentState.currentBlogId,
        commentViewModel = commentVm,
        coroutineScope = coroutineScope,
        tapImage = {
            imagePreviewVm.setImagePreView(arrayOf(it))
            AtNavigation(navController).navigateToImagePreviewPage()
        },
        commentState = commentState,
        onClose = { isCommentVisible.value = false })



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