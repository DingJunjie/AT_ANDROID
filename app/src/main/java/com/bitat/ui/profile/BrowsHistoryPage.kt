package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.router.AtNavigation
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.component.MediaGrid
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.BrowserHistoryViewModel
import kotlinx.coroutines.Dispatchers

/**
 *    author : shilu
 *    date   : 2024/9/5  09:54
 *    desc   : 浏览历史
 */

@Composable
fun BrowserHistoryPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[BrowserHistoryViewModel::class]
    val state by vm.state.collectAsState()

    val PROFILE_TAB_OPTIONS = listOf("作品", "用户")
    val pagerState: PagerState = rememberPagerState(initialPage = state.currentTabIndex) {
        PROFILE_TAB_OPTIONS.size
    }

    LaunchedEffect(Dispatchers.IO) {

        vm.worksHistory()
    }


    Scaffold(topBar = {
        CommonTopBar(modifier = Modifier,
            title = stringResource(id = R.string.setting_browser_history),
            backFn = { AtNavigation(navHostController).navigateToHome() },
            isBg = true,
            paddingStatus = true)

    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ProfileTabBar(pagerState, PROFILE_TAB_OPTIONS) { index ->
                vm.setCurrentTabIndex(index)
            }
            HorizontalPager(state = pagerState,
                modifier = Modifier.fillMaxSize().background(Color.White),
                verticalAlignment = Alignment.Top) { index ->

                when (index) {
                    0 -> {

                        Column(modifier = Modifier.fillMaxSize()) {
                            MediaGrid(mediaList = state.myWorks)

                        }
                    }
                    1 -> {
                        UserHistory(viewModelProvider)
                    }
                }
            }
        }
    }
}

@Composable
fun UserHistory(viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[BrowserHistoryViewModel::class]
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.getUserList(true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(state.userList) { user ->
                FansItem(user, 1,itemTap = {}, followFn = {}, RelationUtils.toFollowContent(user.rel))
            }
        }
    }

}