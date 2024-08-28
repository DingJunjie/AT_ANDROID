package com.bitat.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.CommonTopBar
import com.bitat.viewModel.ProfileViewModel

@Composable
fun FollowsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()

    //    Scaffold(topBar = {
    //        CommonTopBar(title = "关注列表", backFn = { navHostController.popBackStack() })
    //    }, modifier = Modifier.padding(top = statusBarHeight)) { padding ->
    //        Column(modifier = Modifier.padding(padding)) {
    //            state.followsList.forEach {
    //                Text(it.nickname)
    //            }
    //        }
    //    }
    CollapsingToolbarWithContentExample()


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CollapsingToolbarWithContentExample() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val collapseFraction = scrollBehavior.state.collapsedFraction
    val listState = rememberLazyListState()
    var itemVisible = remember { mutableStateOf(true) }

    //    LaunchedEffect(listState) {
    //        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
    //            .collect { visibleItems ->
    //                visibleItems.any {
    //                    CuLog.debug(CuTag.Profile,"当前显示 index ：${it.index}")
    //                    it.index==0
    //                }
    //            }
    //    }


    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { visibleItems ->
                CuLog.debug(CuTag.Profile, "当前显示 firstVisibleItemIndex ：${visibleItems}")
                if (visibleItems == 1) itemVisible.value = true
            }
    }


    Scaffold(topBar = {
        LargeTopAppBar(
            title = {
                Text(text = "Profile Page",
                    fontSize = lerp(24.sp, 16.sp, collapseFraction),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
            },
            scrollBehavior = scrollBehavior,

            )
    }) { innerPadding ->

        LazyColumn(state = listState,
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)) { // Additional content visible only when expanded
            item() {
                AnimatedVisibility( //                    visible = collapseFraction < 0.5f,
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .background(Color.Green)) {
                        Text(text = "Expanded Content 1",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(vertical = 8.dp))
                        Text(text = "Expanded Content 2",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(vertical = 8.dp)) // Add more content as needed
                    }
                }
            }

            // Common content always visible
            items(50) { index ->
                Text(text = "Item #$index",
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}

