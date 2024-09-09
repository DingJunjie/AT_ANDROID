package com.bitat.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.router.AtNavigation
import com.bitat.state.BrowserHistoryState
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.component.MediaGrid
import com.bitat.viewModel.BrowserHistoryViewModel

/**
 *    author : shilu
 *    date   : 2024/9/5  09:54
 *    desc   : 浏览历史
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BrowserHistoryPage(navHostController: NavHostController) {
    val vm = viewModel<BrowserHistoryViewModel>()
    val state: State<BrowserHistoryState> = vm.state.collectAsState()

    val PROFILE_TAB_OPTIONS = listOf("作品", "用户")
    val pagerState: PagerState = rememberPagerState(initialPage = state.value.currentTabIndex) {
        PROFILE_TAB_OPTIONS.size
    }

    LaunchedEffect(Unit){
        vm.worksHistory()
    }

    Scaffold(topBar = {
        CommonTopBar(modifier = Modifier,
            title = stringResource(id = R.string.setting_browser_history),
            backFn = { AtNavigation(navHostController).navigateToHome() },
            isBg = true,
            padingStatus = true)

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
                            MediaGrid(mediaList = state.value.myWorks)

                        }
                    }
                    1 -> { //                        ProfileWorks(userId = userId, navHostController, viewModelProvider)
                    }
                }

            }
        }
    }
}

@Composable
fun WorkHistory(state: State<BrowserHistoryState>) {

}