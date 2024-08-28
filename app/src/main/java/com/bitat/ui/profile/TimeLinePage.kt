package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.Density
import com.bitat.router.AtNavigation
import com.bitat.state.BlogOperation
import com.bitat.utils.ScreenUtils
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
fun TimeLinePage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: TimeLineViewModel = viewModel()
    val state = vm.state.collectAsState()

    val playingIndex = remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Dispatchers.IO) {
        vm.timeLineInit()
    }


    Column(modifier = Modifier.fillMaxSize().height(ScreenUtils.screenHeight.dp)) {
        LazyColumn(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(5.dp)) {
            itemsIndexed(state.value.blogList) { index, item ->
                TimeLineBlogItem(blog = item,
                    isPlaying = index == playingIndex.value,
                    navHostController = navController,
                    viewModelProvider = viewModelProvider,
                    tapComment = { //                        coroutineScope.launch {
                        //                            commentVm.updateBlogId(item.id)
                        //                            delay(1000)
                        //                            currentOperation = BlogOperation.Comment
                        //                        }
                        //                        isCommentVisible.value = true

                    },
                    tapAt = { //                        coroutineScope.launch {
                        //                            delay(1000)
                        //                            currentOperation = BlogOperation.At
                        //                        }
                    },
                    tapLike = { //更新列表中 点赞数据
                        //                        vm.likeClick(item)
                    },
                    tapCollect = { //                        collectTipY = it.div(Density).toInt()
                        //                        collectVm.updateBlog(blog = item)
                        //                        coroutineScope.launch {
                        //                            currentOperation = BlogOperation.Collect
                        //                        }
                        //                        collectTipVisible = true

                        if (item.hasCollect) { // 已收藏，取消
                            //                            collectVm.cancelCollect()
                        } else { // 未收藏，收藏
                            //                            collectVm.collectBlog(0)
                        } //                        vm.collectClick(item)
                        //                        coroutineScope.launch {
                        //                            delay(3000)
                        //                            collectTipVisible = false
                        //                        }

                    },
                    contentClick = { item -> //                        vm.setCurrentBlog(item)
                        //                        AtNavigation(navController).navigateToBlogDetail()
                    },
                    moreClick = { //                        vm.setCurrentBlog(item)
                    })
            }
        }
    }
}