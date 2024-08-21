package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.Density
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
<<<<<<< HEAD
import com.bitat.repository.store.CommentStore
=======
import com.bitat.repository.dto.resp.UserBase1Dto
>>>>>>> 24dd2fe (collect, video and so on)
import com.bitat.router.AtNavigation
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogOperation
import com.bitat.ui.common.RefreshView
<<<<<<< HEAD
import com.bitat.ui.common.SvgIcon
=======
import com.bitat.ui.common.WeDialog
import com.bitat.ui.common.rememberDialogState
>>>>>>> 24dd2fe (collect, video and so on)
import com.bitat.ui.common.rememberLoadMoreState
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.component.AnimatedMenu
<<<<<<< HEAD
=======
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentList
>>>>>>> 24dd2fe (collect, video and so on)
import com.bitat.ui.component.CommentPopup
import com.bitat.ui.theme.white
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/***
 * 首页的数据显示
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BlogPage(modifier: Modifier, navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]
    val state by vm.blogState.collectAsState()
    val pagerState: PagerState = rememberPagerState { 3 }
    val loadMoreState = rememberLoadMoreState {
        CuLog.debug(CuTag.Blog, "loadMoreState") //        vm.initBlogList(blogState.currentMenu)
    }

    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val collectState by collectVm.collectState.collectAsState()

    var currentId by remember {
        mutableLongStateOf(0L)
    }
    LaunchedEffect(state.currentMenu) {
        vm.initBlogList(state.currentMenu)
    }

    var currentOperation by remember {
        mutableStateOf(BlogOperation.None)
    }

    val isOpen = remember {
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

    val listState = rememberLazyListState()
    var previousIndex by remember { mutableStateOf(0) }

    val playingIndex = remember {
        mutableStateOf(0)
    }

    LaunchedEffect(listState) {    // 滚动事件监听
        snapshotFlow { listState.firstVisibleItemScrollOffset }.distinctUntilChanged()
            .collect { _ ->
                if (listState.layoutInfo.visibleItemsInfo.size > 1) {
                    if (listState.layoutInfo.visibleItemsInfo[1].offset < ScreenUtils.screenHeight.div(
                            3) && playingIndex.value != listState.firstVisibleItemIndex + 1) {
                        playingIndex.value = listState.firstVisibleItemIndex + 1
                    }
                }
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { _ ->
            CuLog.debug(CuTag.Blog,
                "previousIndex:$previousIndex,firstVisibleItemIndex;${listState.firstVisibleItemIndex}")
            if (previousIndex < listState.firstVisibleItemIndex && state.topBarShow && previousIndex > 0) {
                vm.topBarState(false)
            } else if (previousIndex > listState.firstVisibleItemIndex && !state.topBarShow && previousIndex > 0) {
                vm.topBarState(true)
            }
            previousIndex = listState.firstVisibleItemIndex
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                if (lastVisibleItemIndex == state.blogList.size - 1) {
                    vm.loadMore()
                }
            }
    }

    val toast = rememberToastState()

    val coroutineScope = rememberCoroutineScope()

    val isCommentVisible = remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(white)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.topBarShow) BlogTopBar(state.currentMenu,
                isOpen.value,
                { isOpen.value = it },
                switchMenu = { vm.switchBlogMenu(it) })
            RefreshView(modifier = Modifier
                .nestedScroll(loadMoreState.nestedScrollConnection)
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxHeight()
                .fillMaxWidth(),
                onRefresh = {
                    CuLog.debug(CuTag.Blog, "onRefresh 回调")
                    vm.initBlogList(state.currentMenu)
                }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
//                    contentAlignment = Alignment.Center
                ) {
                    if (state.blogList.size > 0) {
                        if (state.blogList.first().kind.toInt() == BLOG_VIDEO_ONLY || state.blogList.first().kind.toInt() == BLOG_VIDEO_TEXT) {
                            currentId = state.blogList.first().id
                        }
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(state.blogList) { index, item -> //Text(item.content)
                                Surface(modifier = Modifier.fillMaxWidth()) {
                                    BlogItem(blog = item,
                                        isPlaying = playingIndex.value == index, //                                        isPlaying = false,
                                        navController,
                                        viewModelProvider = viewModelProvider,
                                        tapComment = {
                                            coroutineScope.launch {
                                                commentVm.updateBlogId(item.id)
                                                delay(1000)
                                                currentOperation = BlogOperation.Comment
                                            }
                                            isCommentVisible.value = true
                                        },
                                        tapAt = {
                                            coroutineScope.launch {
                                                delay(1000)
                                                currentOperation = BlogOperation.At
                                            }
                                        },
                                        tapLike = {},
                                        tapCollect = {
                                            collectTipY = it.div(Density).toInt()
                                            collectVm.updateBlog(blog = item)
                                            coroutineScope.launch {
                                                currentOperation = BlogOperation.Collect
                                            }
                                            collectTipVisible = true

                                            if (item.hasCollect) {
                                                // 已收藏，取消
                                                collectVm.cancelCollect()
                                            } else {
                                                // 未收藏，收藏
                                                collectVm.collectBlog(0)
                                            }

                                            coroutineScope.launch {
                                                delay(3000)
                                                collectTipVisible = false
                                            }

                                        },
                                        contentClick = { item ->
                                            vm.setCurrentBlog(item)
                                            AtNavigation(navController).navigateToBlogDetail()
                                        },
                                        moreClick = {
                                            vm.setCurrentBlog(item)
                                        })
                                }
                            }
                        }
                    } else {
                        when (state.currentMenu) {
                            BlogMenuOptions.Recommend -> Text(text = "推荐" + stringResource(R.string.no_data))
                            BlogMenuOptions.Latest -> Text(text = "最新" + stringResource(R.string.no_data))
                            BlogMenuOptions.Followed -> Text(text = "关注" + stringResource(R.string.no_data))
                        }
                    }
                }
            }
        }
    }


    CollectTips(collectTipVisible, y = collectTipY, closeTip = {
        collectTipVisible = false
    }, openPopup = {
        collectTipVisible = false
        collectPopupVisible = true
    })

    CollectPopup(
        visible = collectPopupVisible,
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
        }
    )

    CommentPopup(
        visible = isCommentVisible.value,
        blogId = commentState.currentBlogId,
        commentViewModel = commentVm,
        coroutineScope = coroutineScope,
        tapImage = {
            imagePreviewVm.setImagePreView(arrayOf(it))
            AtNavigation(navController).navigateToImagePreviewPage()
        },
        commentState = commentState,
        onClose = { isCommentVisible.value = false })


}


@Composable
fun BlogTopBar(currentMenu: BlogMenuOptions, isOpen: Boolean, toggleMenu: (Boolean) -> Unit, switchMenu: (BlogMenuOptions) -> Unit) {
    Row(modifier = Modifier.height(30.dp).padding(start = 5.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Start) {
        AnimatedMenu<BlogMenuOptions>(currentMenu, isOpen, toggleMenu) {
            switchMenu(it)
        }
        Row(modifier = Modifier.fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End) {
            SvgIcon(path = "svg/search.svg", tint = Color.Black, contentDescription = "")
        }
    }
}



