package com.bitat.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.ext.clickableWithoutRipple
import com.bitat.repository.consts.APP_NOTICE_FOLLOW
import com.bitat.repository.po.NoticeMsgPo
import com.bitat.state.Notification
import com.bitat.ui.component.BackButton
import com.bitat.ui.profile.CollectionTab
import com.bitat.ui.profile.PraiseHistory
import com.bitat.ui.profile.ProfileWorks
import com.bitat.ui.profile.TimeLinePage
import com.bitat.ui.theme.Typography
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.NotificationViewModel
import com.google.android.material.tabs.TabLayout.TabView
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[NotificationViewModel::class]
    val state by vm.state.collectAsState()

    val options = listOf("消息", "@和动态", "系统通知")
    val pagerState = rememberPagerState {
        options.size
    }
    val currentPage = remember {
        mutableIntStateOf(0)
    }

    val msgScrollableState = rememberScrollableState {
        it
    }

    Scaffold { padding ->
        Column {
            NotificationTopBar(padding, backFn = { navHostController.popBackStack() })
            Surface(modifier = Modifier.padding(top = 40.dp)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    verticalAlignment = Alignment.Top
                ) { index ->
                    when (index) {
                        0 -> Box(
                            modifier = Modifier
                                .fillMaxSize()
//                                .scrollable(msgScrollableState, orientation = Orientation.Vertical)
                                .background(Color.Yellow)
                        ) {
                            MessageList(navHostController, state.notifications)
                        }

                        1 -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Blue)
                        )

                        2 -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Cyan)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .height(80.dp)
                .offset(y = 160.dp, x = ScreenUtils.screenWidth.times(0.05).dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationMenu(pagerState, options) {
                currentPage.intValue = it
            }
        }
    }
}

@Composable
fun MessageList(navHostController: NavHostController, list: List<Notification>) {
    Column(modifier = Modifier.fillMaxSize()) {
        list.map {
            MessageItem(navHostController, it)
        }
    }
}

@Composable
fun MessageItem(navHostController: NavHostController, notice: Notification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .clickable {
                when (notice.kind) {

                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(url = notice.profile)
        Text(
            notice.displayContent,
            style = Typography.bodySmall.copy(fontSize = 14.sp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        if (notice.time > 0) Text(
            TimeUtils.timeToMD(notice.time),
            style = Typography.bodySmall.copy(fontSize = 12.sp, color = Color.Gray)
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationMenu(pagerState: PagerState, options: List<String>, onSelect: (Int) -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth(0.9f), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TabRow(selectedTabIndex = pagerState.currentPage,
//                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .width(10.dp),
                        height = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), divider = {}) {
                options.forEachIndexed { index, item ->
                    val selected = index == pagerState.currentPage
                    Text(text = item, color = if (selected) {
                        Color.Black
                    } else {
                        Color.Gray
                    }, fontSize = 17.sp, modifier = Modifier
                        .clickableWithoutRipple {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                                onSelect(index)
                            }
                        }
                        .fillMaxWidth()
                        .padding(vertical = 16.dp), textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationTopBar(paddingValues: PaddingValues, backFn: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
    ) {

        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = "https://ww3.sinaimg.cn/mw690/7772612dgy1hqrfwx6bjpj20m80m8q4t.jpg",
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton {
                backFn()
            }
        }
    }
}