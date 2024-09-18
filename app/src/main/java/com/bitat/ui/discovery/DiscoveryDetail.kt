package com.bitat.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.MainCo
import com.bitat.ext.Density
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.BlogContent
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.AtPopup
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.BlogText
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentPopup
import com.bitat.ui.component.UserInfoWithAvatar
import com.bitat.utils.ImageUtils
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.DiscoveryViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DiscoveryDetailPage(
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[DiscoveryViewModel::class]
    val state by vm.discoveryState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val dragStartY = remember {
        mutableFloatStateOf(0f)
    }
    val latestY = remember {
        mutableFloatStateOf(0f)
    }
    val historyCache = remember { mutableListOf<Int>() }

    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val collectState by collectVm.collectState.collectAsState()
    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]

    val isCommentVisible = remember {
        mutableStateOf(false)
    }
    var currentId by remember {
        mutableLongStateOf(0L)
    }


    var currentOperation by remember {
        mutableStateOf(com.bitat.state.BlogOperation.None)
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
    val toast = rememberToastState()


    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect { layoutInfo ->
            layoutInfo.visibleItemsInfo.forEachIndexed { index, lazyListItemInfo ->
                if (!historyCache.contains(lazyListItemInfo.index)) {
                    historyCache.add(lazyListItemInfo.index)
                    if (state.discoveryList.size > lazyListItemInfo.index) vm.addHistory(state.discoveryList[lazyListItemInfo.index])
                }
            }
        }

    }
    DisposableEffect(Unit) {
        onDispose {
            historyCache.clear()
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            latestY.floatValue =
                                change.position.y //                            println("scrolling $change, $dragAmount")
                        }, onDragEnd = {
                            val offset = latestY.floatValue - dragStartY.floatValue
                            if (offset > 50f) { // 下滚
                                println("hello")
                                coroutineScope.launch {
                                    listState.firstVisibleItemScrollOffset
                                    val targetItem = listState.firstVisibleItemIndex - 1
                                    if (targetItem >= 0) {
                                        listState.animateScrollToItem(targetItem)
                                    }
                                }
                            } else if (offset < -50f) { // 上滚
                                println("world")
                                coroutineScope.launch {
                                    val targetItem = listState.firstVisibleItemIndex + 1
                                    if (targetItem < listState.layoutInfo.totalItemsCount) {
                                        listState.animateScrollToItem(targetItem)
                                    }
                                }
                            }
                            print("start at ${dragStartY.floatValue}, end at ${latestY.floatValue}")
                            dragStartY.floatValue = 0f
                            latestY.floatValue = 0f
                        }, onDragStart = {
                            dragStartY.floatValue = it.y
                        })
                    },
                userScrollEnabled = false
            ) {
                items(state.discoveryList) { item ->
                    DiscoveryItem(
                        item,
                        navHostController,
                        viewModelProvider,
                        tapComment = {
                            coroutineScope.launch {
                                commentVm.updateBlogId(item.id)
                                delay(1000)
                                currentOperation = com.bitat.state.BlogOperation.Comment
                            }
                            isCommentVisible.value = true
                        }, {
                            coroutineScope.launch {
                                delay(1000)
                                currentOperation = com.bitat.state.BlogOperation.At
                            }
                            atPopupVisible = true
                        }, {

                        }, {
                            collectTipY = it.div(Density).toInt()
                            coroutineScope.launch {
                                currentOperation = com.bitat.state.BlogOperation.Collect
                            }
                            collectTipVisible = true
                            collectVm.updateBlog(blog = item)
                            if (item.hasCollect) { // 已收藏，取消
                                collectVm.cancelCollect() {
                                    vm.updateCollectStatus(item, false)
                                }
                            } else { // 未收藏，收藏
                                collectVm.collectBlog(0) {
                                    vm.updateCollectStatus(item, true)
                                }
                            }
                            coroutineScope.launch {
                                delay(3000)
                                collectTipVisible = false
                            }
                        })
                    HorizontalDivider(modifier = Modifier.padding(vertical = statusBarHeight / 2))
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
            AtNavigation(navHostController).navigateToImagePreviewPage()
        },
        commentState = commentState,
        onClose = { isCommentVisible.value = false }) {
//        state.currentBlog?.let {
//            it.comments += 1u
//            vm.setCurrentBlog(it)
//            vm.refreshCurrent(it)
//        }
    }

//    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
//        Button(onClick = {
//            coroutineScope.launch {
//                listState.firstVisibleItemScrollOffset
//                val targetItem = listState.firstVisibleItemIndex - 1
//                if (targetItem >= 0) {
//                    listState.animateScrollToItem(targetItem)
//                }
//            }
//        }) {
//            Text("up")
//        }
//
//        Button(onClick = {
//            coroutineScope.launch {
//                val targetItem = listState.firstVisibleItemIndex + 1
//                if (targetItem < listState.layoutInfo.totalItemsCount) {
//                    listState.animateScrollToItem(targetItem)
//                }
//            }
//        }, modifier = Modifier.padding(start = 200.dp)) {
//            Text("down")
//        }
//    }
}

@Composable
fun DiscoveryItem(
    item: BlogBaseDto,
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider,
    tapComment: () -> Unit = {},
    tapAt: () -> Unit = {},
    tapLike: () -> Unit = {},
    tapCollect: (Int) -> Unit = {},
    updateFlag: Int = 0
) {
    var height = 0
    if (item.kind >= 2) {
        val size = ImageUtils.getParamsFromUrl(item.cover)
        height = ImageUtils.getHeight(size, ScreenUtils.screenWidth)
        println("current height is $height")
    }
    val maxHeight = (ScreenUtils.screenHeight * 0.7).toInt()
    val calHeight = if (height > maxHeight) maxHeight else height // 高度不能超过屏幕高度的70%
    Column() {
        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
            UserInfoWithAvatar(nickname = item.nickname, avatar = item.profile)
        }
        BlogText(content = item.content)
        //        Box(
        //            modifier = Modifier
        //                .fillMaxWidth()
        //                .height(100.dp)
        //                .background(color = Color.Cyan)
        //        )
        BlogContent(
            kind = item.kind.toInt(),
            mBlogBaseDto = item,
            maxHeight = calHeight,
            needRoundedCorner = false,
            needStartPadding = false,
            navHostController = navHostController,
            viewModelProvider = viewModelProvider
        )
        Box(modifier = Modifier.padding(start = 10.dp)) {
            BlogOperation(
                blog = item,
                tapComment,
                tapAt,
                tapLike,
                tapCollect,
                updateFlag
            )
        }
    }
}

