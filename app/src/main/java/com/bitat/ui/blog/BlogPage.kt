package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.Density
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.router.AtNavigation
import com.bitat.router.NavigationItem
import com.bitat.state.BlogDetailsType
import com.bitat.state.BlogLoad
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogOperation
import com.bitat.ui.common.RefreshView
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberLoadMoreState
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.AnimatedMenu
import com.bitat.ui.component.AtPopup
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentPopup
import com.bitat.ui.theme.lineColor
import com.bitat.ui.theme.white
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogDetailsViewModel
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.HomeViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/***
 * 首页的数据显示
 */
@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType")
@Composable
fun BlogPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]
    val state by vm.blogState.collectAsState()
    val loadMoreState = rememberLoadMoreState {
        CuLog.debug(CuTag.Blog, "loadMoreState") //        vm.initBlogList(blogState.currentMenu)
    }

    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val collectState by collectVm.collectState.collectAsState()

    val homeVm: HomeViewModel = viewModelProvider[HomeViewModel::class]
    val homeState by homeVm.homeState.collectAsState()

    val detailsVm: BlogDetailsViewModel = viewModelProvider[BlogDetailsViewModel::class]

    var currentId by remember {
        mutableLongStateOf(0L)
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

    var atPopupVisible by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = state.currentListIndex,
        initialFirstVisibleItemScrollOffset = state.listOffset
    )
    var previousIndex by remember { mutableStateOf(0) }

    val playingIndex = remember {
        mutableStateOf(0)
    }

    LaunchedEffect(state.currentMenu) {
        if (state.isFirst) {
            vm.initBlogList(state.currentMenu, isRefresh = true)
            vm.firstFetchFinish()
        } else {
            //            if (state.currentListIndex > 0) {
            //                listState.scrollToItem(state.currentListIndex)
            //            }
        }

        //        vm.initBlogList(state.currentMenu, isRefresh = true)
        //        CuLog.debug(CuTag.Blog, "加载数据，${state.currentMenu}")
    }

    LaunchedEffect(listState) {    // 滚动事件监听
        snapshotFlow { listState.firstVisibleItemScrollOffset }.distinctUntilChanged()
            .collect { _ ->
                if (listState.layoutInfo.visibleItemsInfo.size > 1) {
                    if (listState.layoutInfo.visibleItemsInfo[1].offset < ScreenUtils.screenHeight.div(
                            3
                        ) && playingIndex.value != listState.firstVisibleItemIndex + 1
                    ) {
                        playingIndex.value = listState.firstVisibleItemIndex + 1
                    }
                }
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { _ ->
            CuLog.debug(
                CuTag.Blog,
                "previousIndex:$previousIndex,firstVisibleItemIndex;${listState.firstVisibleItemIndex}"
            )

            if (previousIndex < listState.firstVisibleItemIndex && state.topBarShow && previousIndex > 0) {
                vm.topBarState(false)
            } else if (previousIndex > listState.firstVisibleItemIndex && !state.topBarShow && previousIndex > 0) {
                vm.topBarState(true)
            }
            previousIndex = listState.firstVisibleItemIndex
            vm.listCurrent(listState.firstVisibleItemIndex)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect { layoutInfo ->
            CuLog.debug(CuTag.Blog, "layoutInfo，当前显示item ${layoutInfo.visibleItemsInfo}")

            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
            if (lastVisibleItemIndex == state.blogList.size - 1) {
                vm.isLoadMore(true)
                vm.loadMore() {
                    ToastModel("加载更多成功", ToastModel.Type.Success).showToast()
                }
            }

            // 添加观看记录


        }
    }



    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }.collect { firstItem ->
            CuLog.debug(CuTag.Blog, "list滑动,当前位置：$firstItem")
            vm.listOffSet(firstItem)
        }
    }
    DisposableEffect(Unit) {
        onDispose {

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
                .padding()
                .fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight)
            )
            if (state.topBarShow) BlogTopBar(
                state.currentMenu,
                isOpen.value,
                { isOpen.value = it },
                switchMenu = { vm.switchBlogMenu(it) },
                navController
            )
            RefreshView(modifier = Modifier
                .nestedScroll(loadMoreState.nestedScrollConnection)
                .fillMaxHeight()
                .fillMaxWidth(), onRefresh = {
                vm.initBlogList(state.currentMenu)
            }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                    //          .padding(bottom = dimensionResource(R.dimen.home_tab_height)),
                    //           contentAlignment = Alignment.Center
                ) {
                    if (state.blogList.size > 0) {
                        if (state.blogList.first().kind.toInt() == BLOG_VIDEO_ONLY || state.blogList.first().kind.toInt() == BLOG_VIDEO_TEXT) {
                            currentId = state.blogList.first().id
                        }

                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(state.blogList) { index, item ->
                                Surface(modifier = Modifier.fillMaxWidth()) {
                                    BlogItem(blog = item,
                                        isPlaying = playingIndex.value == index, //                                        isPlaying = false,
                                        navController,
                                        viewModelProvider = viewModelProvider,
                                        tapComment = {
                                            vm.setCurrentBlog(item)
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
                                            atPopupVisible = true
                                        },
                                        tapLike = { //更新列表中 点赞数据
                                            vm.likeClick(item)
                                        },
                                        tapCollect = {
                                            collectTipY = it.div(Density).toInt()
                                            collectVm.updateBlog(blog = item)
                                            coroutineScope.launch {
                                                currentOperation = BlogOperation.Collect
                                            }
                                            collectTipVisible = true

                                            if (item.hasCollect) { // 已收藏，取消
                                                collectVm.cancelCollect() {
                                                    vm.collectClick(item)
                                                }
                                            } else { // 未收藏，收藏
                                                collectVm.collectBlog(0) {
                                                    vm.collectClick(item)
                                                }
                                            }
                                            coroutineScope.launch {
                                                delay(3000)
                                                collectTipVisible = false
                                            }
                                        },
                                        contentClick = { item ->
                                            detailsVm.pageType(BlogDetailsType.BlogList)
                                            detailsVm.setCurrentBlog(item)
                                            vm.setCurrentBlog(item)
                                            AtNavigation(navController).navigateToBlogDetail()
                                        },
                                        moreClick = {
                                            vm.setCurrentBlog(item)
                                        }, onRemove = {
                                            vm.removeOne(blog = item)
                                        })
                                }
                            }

                            // 显示加载更多的进度条
                            if (state.isLoadMore) {
                                item {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 10.dp, bottom = 10.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically

                                        ) {
                                            if (state.loadResp == BlogLoad.Default) CircularProgressIndicator(
                                                modifier = Modifier.size(30.dp)
                                            )
                                            Spacer(modifier = Modifier.width(20.dp))

                                            Text(
                                                text = when (state.loadResp) {
                                                    BlogLoad.NoData -> "再试一次"

                                                    BlogLoad.Success -> "加载成功"
                                                    BlogLoad.Fail -> "加载失败，网络错误"
                                                    BlogLoad.TimeOut -> "加载失败，网络超时"
                                                    BlogLoad.Default -> "数据加载中"
                                                }
                                            )

                                        }
                                        TextButton(onClick = { vm.loadMore() { } }) {
                                            Text(text = "再试一次")
                                        }


                                    }

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
                    if (state.flag < 0) Text(text = "")
                }
            }
        }
        CollectTips(collectTipVisible, y = collectTipY, closeTip = {
            collectTipVisible = false
        }, openPopup = {
            collectTipVisible = false
            collectPopupVisible = true
        })
    }

    AtPopup(visible = atPopupVisible) {
        atPopupVisible = false
    }

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

    CommentPopup(visible = isCommentVisible.value,
        blogId = commentState.currentBlogId,
        commentViewModel = commentVm,
        coroutineScope = coroutineScope,
        tapImage = {
            imagePreviewVm.setImagePreView(arrayOf(it))
            AtNavigation(navController).navigateToImagePreviewPage()
        },
        commentState = commentState,
        onClose = { isCommentVisible.value = false }) {
        state.currentBlog?.let {
            it.comments += 1u
            vm.setCurrentBlog(it)
            vm.refreshCurrent(it)
        }
    }
}

@Composable
fun BlogContainer() {

}


@Composable
fun BlogTopBar(
    currentMenu: BlogMenuOptions,
    isOpen: Boolean,
    toggleMenu: (Boolean) -> Unit,
    switchMenu: (BlogMenuOptions) -> Unit,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .padding(start = 5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        AnimatedMenu<BlogMenuOptions>(currentMenu, isOpen, toggleMenu) {
            switchMenu(it)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                .clickable(onClick = {
                    navController.navigate(NavigationItem.Search.route)
                }, indication = null, interactionSource = remember { MutableInteractionSource() }),
            horizontalArrangement = Arrangement.End
        ) {
            SvgIcon(path = "svg/search.svg", tint = Color.Black, contentDescription = "")
        }
    }
}


