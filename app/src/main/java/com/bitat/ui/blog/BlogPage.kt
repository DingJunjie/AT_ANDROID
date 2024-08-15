package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.router.AtNavigation
import com.bitat.state.BlogMenuOptions
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.WeRefreshView
import com.bitat.ui.common.rememberLoadMoreState
import com.bitat.ui.component.AnimatedMenu
import com.bitat.ui.theme.white
import com.bitat.viewModel.BlogViewModel

/***
 * 首页的数据显示
 */
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BlogPage(modifier: Modifier, navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val state by vm.blogState.collectAsState() //获取 状态栏高度 用于设置上边距
    val blogState by vm.blogState.collectAsState()
    val pagerState: PagerState = rememberPagerState { 3 }
    val loadMoreState = rememberLoadMoreState {
        CuLog.debug(CuTag.Blog, "loadMoreState") //        vm.initBlogList(blogState.currentMenu)
    }

    var currentId by remember {
        mutableLongStateOf(0L)
    }
    LaunchedEffect(state.currentMenu) {
        vm.initBlogList(state.currentMenu)
    }

    val isOpen = remember {
        mutableStateOf(false)
    }

    Scaffold(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(white)) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            BlogTopBar(state.currentMenu,
                isOpen.value,
                { isOpen.value = it },
                switchMenu = { vm.switchBlogMenu(it) })
            WeRefreshView(modifier = Modifier.nestedScroll(loadMoreState.nestedScrollConnection),
                onRefresh = {
                    CuLog.debug(CuTag.Blog, "onRefresh 回调")
                    vm.initBlogList(blogState.currentMenu)
                }) {
                Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (state.blogList.size > 0) {
                            if (state.blogList.first().kind.toInt() == BLOG_VIDEO_ONLY || state.blogList.first().kind.toInt() == BLOG_VIDEO_TEXT) {
                                currentId = state.blogList.first().id
                            }
                        }
                        items(state.blogList) { item -> //Text(item.content)
                            Surface(modifier = Modifier.clickable(onClick = {
                                vm.setCurrentBlog(item)
                                AtNavigation(navController).navigateToBlogDetail()
                            }).fillMaxWidth()) {
                                BlogItem(blog = item,
                                    currentId = currentId,
                                    isCurrent = { //更新video显示状态
                                        currentId = it
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
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
            SvgIcon(path = "svg/search.svg",
                tint = androidx.compose.ui.graphics.Color.Black,
                contentDescription = "")
        }
    }
}




