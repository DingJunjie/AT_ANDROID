package com.bitat.ui.discovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.core.ui.components.refreshview.WeRefreshView
import com.bitat.core.ui.components.refreshview.rememberLoadMoreState
import com.bitat.ext.clickableWithoutRipple
import com.bitat.feature.samples.videochannel.data.ImageData.Listdata
import com.bitat.feature.samples.videochannel.data.ImageData.Listdata_
import com.bitat.router.AtNavigation
import com.bitat.ui.component.WeLoadMore
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import kotlin.random.Random
import kotlin.time.Duration

enum class MenuOptions {
    DISCOVERY, PODCAST, ACTIVITY;

    fun getUiContent(): String {
        return when (this) {
            DISCOVERY ->
                "探索"

            PODCAST ->
                "播客"

            ACTIVITY ->
                "活动"
        }
    }
}

/****
 * 探索
 */
@Composable
fun DiscoveryPage(navController: NavHostController) {

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val height = screenWidthDp / 3

    val currentMenu = remember {
        mutableStateOf(MenuOptions.DISCOVERY)
    }

    val isMenuOpen = remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = statusBarHeight),
        topBar = {
            DiscoveryTopBar(
                navController,
                currentMenu.value,
                isOpen = isMenuOpen.value,
                toggleMenu = { isMenuOpen.value = it },
                switchMenu = {
                    currentMenu.value = it
                })
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {

            val listState = rememberLazyListState()
            val listItems = remember {
                mutableStateListOf<ListItemData>().apply {
                    addAll(Listdata)
                }
            }

            val loadMoreState = rememberLoadMoreState {
                listItems.addAll(Listdata_)
            }

            WeRefreshView(
                modifier = Modifier.nestedScroll(loadMoreState.nestedScrollConnection),
                onRefresh = {
//                listItems.clear()
//                listItems.addAll(Listdata)
                    AtNavigation(navController).navigateToVideo()

                }
            ) {
                //竖向瀑布流
                LazyVerticalStaggeredGrid(listItems, height)

                if (loadMoreState.isLoadingMore) {
                    WeLoadMore(listState = listState)
                }


//            LazyColumn(state = listState, modifier = Modifier.cardList()) {
//                items(listItems, key = { it }) {
//                    WeCardListItem(label = "第${it}行")
//                }
//                item {
//                    if (loadMoreState.isLoadingMore) {
//                        WeLoadMore(listState = listState)
//                    }
//                }
//            }
            }
        }
    }

}

@Composable
fun LazyVerticalStaggeredGrid(items: List<ListItemData>, height: Dp) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyStaggeredGridState(),
        contentPadding = PaddingValues(5.dp),//左右;两边的距离
        horizontalArrangement = Arrangement.spacedBy(1.dp),//横向图片两边的距离
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true,//瀑布流是否可滑动
    ) {
        items(items) { item ->
            VerticalRandomColorBox(item = item, height = height)
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
fun VerticalRandomColorBox(item: ListItemData, height: Dp) {

    val random = Random.nextInt(1, 3)
    val rHeight = height * random
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .height(if (random == 1) rHeight - 2.dp else rHeight)
            .clickable {

            }
    ) {
        Box(
            Modifier
                .padding(2.dp)
        ) {
            AsyncImage(
                model = item.thumbnailUrl,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
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
                if (item.type == 1) SvgIcon(
                    path = "svg/create_image.svg",
                    tint = Color.White,
                    contentDescription = ""
                ) else if (item.type == 2) SvgIcon(
                    path = "svg/video-icon-fill.svg",
                    tint = Color.White,
                    contentDescription = ""
                )

            }
        }

    }

}

@Composable
fun DiscoveryTopBar(
    navController: NavHostController,
    currentMenu: MenuOptions,
    isOpen: Boolean,
    toggleMenu: (Boolean) -> Unit,
    switchMenu: (MenuOptions) -> Unit
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
                .fillMaxWidth(0.9f)
                .padding(start = 5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DiscoveryMenu(currentMenu, isOpen, toggleMenu, switchMenu = { switchMenu(it) })
            Box(modifier = Modifier.weight(1f)) {
                SearchInputButton(navController)
            }
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Menu, contentDescription = "")
        }
    }
}

@Composable
fun DiscoveryMenu(
    currentMenu: MenuOptions,
    isOpen: Boolean = false,
    toggleMenu: (Boolean) -> Unit,
    switchMenu: (MenuOptions) -> Unit
) {
    val rotateVal by animateFloatAsState(targetValue = if (isOpen) 90f else 270f, label = "")
    Surface(
        modifier = Modifier
            .height(60.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(isOpen || currentMenu == MenuOptions.DISCOVERY) {
                MenuItem(
                    MenuOptions.DISCOVERY,
                    currentMenu
                ) {
                    switchMenu(MenuOptions.DISCOVERY)
                }
            }
            AnimatedVisibility(isOpen || currentMenu == MenuOptions.PODCAST) {
                MenuItem(
                    MenuOptions.PODCAST,
                    currentMenu
                ) {
                    switchMenu(MenuOptions.PODCAST)
                }
            }
            AnimatedVisibility(isOpen || currentMenu == MenuOptions.ACTIVITY) {
                MenuItem(
                    MenuOptions.ACTIVITY,
                    currentMenu
                ) {
                    switchMenu(MenuOptions.ACTIVITY)
                }
            }
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "",
                modifier = Modifier
                    .rotate(rotateVal)
                    .clickableWithoutRipple {
                        toggleMenu(!isOpen)
                    }
            )
        }
    }
}

@Composable
fun MenuItem(
    menuOptions: MenuOptions,
    currentMenu: MenuOptions,
    choseMenu: () -> Unit
) {
    Box(modifier = Modifier
        .padding(3.dp)
        .clickable { choseMenu() }) {
        Text(
            menuOptions.getUiContent(),
            fontWeight = if (menuOptions == currentMenu) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}
