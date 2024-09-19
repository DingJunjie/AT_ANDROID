package com.bitat.ui.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.ui.common.RefreshView
import com.bitat.ui.common.rememberLoadMoreState
import com.bitat.router.NavigationItem
import com.bitat.state.DiscoveryMenuOptions
import com.bitat.ui.common.WeLoadMore
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.AnimatedMenu
import com.bitat.viewModel.DiscoveryViewModel
import kotlin.time.Duration


/****
 * 探索
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiscoveryPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[DiscoveryViewModel::class]
    val state by vm.discoveryState.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val height = screenWidthDp / 3

    LaunchedEffect(Unit) {
        if (state.isFirst) {
            vm.getDiscoveryList()
            vm.firstFetchFinish()
        }
    }

    val isMenuOpen = remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()

    val loadMoreState = rememberLoadMoreState {
        vm.getDiscoveryList(isRefresh = false)
    }

    val isRefreshing = remember {
        mutableStateOf(false)
    }
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing.value, onRefresh = {
        vm.getDiscoveryList(isRefresh = true)
    })


    Scaffold(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = statusBarHeight),
        topBar = {
            DiscoveryTopBar(navController,
                state.currentMenu,
                isOpen = isMenuOpen.value,
                toggleMenu = { isMenuOpen.value = it },
                switchMenu = {
                    vm.switchMenu(it)
                })
        }) { padding ->
//        Box(
//            modifier = Modifier
//                .padding(padding)
//                .pullRefresh(pullRefreshState)
//        ) {
        PullRefreshIndicator(
            refreshing = isRefreshing.value, state = pullRefreshState, scale = true
        )
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            RefreshView(modifier = Modifier
                .nestedScroll(loadMoreState.nestedScrollConnection)
                .fillMaxSize(), onRefresh = {
                vm.getDiscoveryList(isRefresh = true)
            }) {
                // 竖向瀑布流
                LazyVerticalStaggeredGrid(state.discoveryList, height, navController)

                if (loadMoreState.isLoadingMore) {
                    WeLoadMore(listState = listState)
                }
            }
        }
//        }
    }
}

@Composable
fun LazyVerticalStaggeredGrid(
    items: List<BlogBaseDto>, height: Dp, navController: NavHostController
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f),
        state = rememberLazyStaggeredGridState(),
        contentPadding = PaddingValues(0.dp),//左右;两边的距离
        horizontalArrangement = Arrangement.spacedBy(0.dp),//横向图片两边的距离
        flingBehavior = ScrollableDefaults.flingBehavior(),
        verticalItemSpacing = 0.dp,
        userScrollEnabled = true,//瀑布流是否可滑动
    ) {
        items(items) { item ->
            VerticalRandomColorBox(item = item, height = height, tapFn = {
                navController.navigate(NavigationItem.DiscoveryDetail.route)
            })
        }
    }
}

data class ListItemData(
    val videoUrl: String,//视频路径
    val thumbnailUrl: String,//路径
    val duration: Duration,//视频时长
    val type: Int,//1 图片 2 视频
)


@Composable
fun VerticalRandomColorBox(item: BlogBaseDto, height: Dp, tapFn: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(1.dp)
        .height(if (item.height == 1) height - 1.dp else height.times(2))
        .clickable {
            tapFn()
        }) {
        Box {
            AsyncImage(
                model = item.cover,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .fillMaxWidth(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                if (item.kind in 2..3) SvgIcon(
                    path = "svg/create_image.svg",
                    tint = Color.White,
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                ) else if (item.kind in 4..7) SvgIcon(
                    path = "svg/video-icon-fill.svg",
                    tint = Color.White,
                    contentDescription = "",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp, end = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DiscoveryTopBar(
    navController: NavHostController,
    currentMenu: DiscoveryMenuOptions,
    isOpen: Boolean,
    toggleMenu: (Boolean) -> Unit,
    switchMenu: (DiscoveryMenuOptions) -> Unit
) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            AnimatedMenu<DiscoveryMenuOptions>(
//                currentMenu,
//                isOpen,
//                toggleMenu,
//                switchMenu = { switchMenu(it) })
            Box(modifier = Modifier.padding(start = 5.dp, end = 10.dp)) {
                SvgIcon(path = "svg/discovery-menu.svg", contentDescription = "")
            }
            Box(modifier = Modifier.weight(1f)) {
                SearchInputButton(navController)
            }
        }
//        IconButton(onClick = { /*TODO*/ }) {
//            Icon(Icons.Filled.Menu, contentDescription = "")
//        }
    }
}
