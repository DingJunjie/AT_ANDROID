package com.bitat.ui.profile

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.Density
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.AtNavigation
import com.bitat.state.BlogOperation
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.component.CollectPopup
import com.bitat.ui.component.CollectTips
import com.bitat.ui.component.CommentPopup
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/8/27  17:47
 *    desc   :
 */

@Composable
fun TimeLinePage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModel()
    val state = vm.blogState.collectAsState()

    val playingIndex = remember {
        mutableStateOf(0)
    }
    LaunchedEffect(Dispatchers.IO) {
        if (state.value.isFirst) {
            vm.timeLineInit()
            vm.firstFetchFinish()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = ScreenUtils.screenHeight.dp)
            .padding(start = 5.dp, end = 5.dp)
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
                tapAt = {
                },
                tapLike = { //更新列表中 点赞数据
                    vm.likeClick(item)
                },
                tapCollect = {
                    collectTipY = it.div(Density).toInt()
                    collectVm.updateBlog(blog = item)
                    collectTipVisible = true

                    if (item.hasCollect) { // 已收藏，取消
                        collectVm.cancelCollect()
                    } else { // 未收藏，收藏
                        collectVm.collectBlog(0)
                    }
                    vm.collectClick(item)
                    coroutineScope.launch {
                        delay(3000)
                        collectTipVisible = false
                    }

                },
                contentClick = { item ->
                    vm.setCurrentBlog(item)
                    AtNavigation(navController).navigateToBlogDetail()
                },
                moreClick = { //                        vm.setCurrentBlog(item)
                })
        }

//        LazyColumn(modifier = Modifier.fillMaxWidth().onGloballyPositioned { layoutCoordinates ->
//            CuLog.debug(CuTag.Profile, "列表高度 ${layoutCoordinates.size.height},lazyColumnHeight ${lazyColumnHeight.value}")
//
//            if (lazyColumnHeight.value != layoutCoordinates.size.height) {
//                lazyColumnHeight.value = layoutCoordinates.size.height
////                    with(density) {
////                    layoutCoordinates.size.height.toDp()
////                }
//            }
//        }, contentPadding = PaddingValues(5.dp),
//            userScrollEnabled = false
//        ) {
//            itemsIndexed(state.value.blogList) { index, item ->
//
//            }
//        }

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

    CollectTips(collectTipVisible, y = collectTipY, closeTip = {
//        collectTipVisible = false
    }, openPopup = {
//        collectTipVisible = false
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
            })
            collectPopupVisible = false
        },
        onClose = {
            collectPopupVisible = false
        })
}