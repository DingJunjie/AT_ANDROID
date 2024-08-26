package com.bitat.ui.chat

import GroupList
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavHostController
import com.bitat.Local
import com.bitat.ui.common.ToastState
import com.bitat.ui.common.rememberToastState
import com.bitat.router.AtNavigation
import com.bitat.ui.common.SwipeActionItem
import com.bitat.ui.common.SwipeActionStyle
import com.bitat.ui.common.SwipeActionType
import com.bitat.ui.common.WeSwipeAction
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.ui.common.rememberAsyncPainter
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatPage(navHostController: NavHostController) {
    val tabPager: PagerState = rememberPagerState(0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val options = remember {
        listOf(
            SwipeActionItem(
                type = SwipeActionType.PLAIN,
                label = "喜欢",
                icon = Icons.Outlined.FavoriteBorder
            ),
            SwipeActionItem(
                type = SwipeActionType.WARNING,
                label = "收藏",
                icon = Icons.Outlined.Star
            ),
            SwipeActionItem(
                type = SwipeActionType.DANGER,
                label = "删除",
                icon = Icons.Outlined.Delete
            )
        )
    }
    val toast = rememberToastState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TopAppBar(title = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null)
                }

                Row {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                }
            }
        }, modifier = Modifier.padding(end = 10.dp))
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
        ) {
            ChatTab(tabPager, switchFun = {
                coroutineScope.launch {
                    tabPager.animateScrollToPage(it)
                }
            })
        }

        HorizontalPager(state = tabPager) { index ->
            when (index) {
                0 -> Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color(0xfff5f5f5))
                    .padding(horizontal = 8.dp)
                    .clickable { }) {
                    ChatList(options, toast) {
                        AtNavigation(navHostController).navigateToChatDetailsPage()
                    }
                }

                1 -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    GroupList()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatTab(tabState: PagerState, switchFun: (Int) -> Unit) {
    val blockOffset by animateFloatAsState(
        targetValue = if (tabState.currentPage == 0) 0f else ScreenUtils.screenWidth * 0.4f,
        label = ""
    ) //
    Surface(
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = (ScreenUtils.screenWidth * 0.08).dp)
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .background(Color(0xfff2f2f2)),
        ) {}
    }

    Surface(
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(0.6f)
            .padding(horizontal = (ScreenUtils.screenWidth * 0.06 + 15).dp, vertical = 5.dp)
            .offset(x = blockOffset.dp)
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .background(Color.White)
        )
    }

    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        ChatTabItem(content = "聊天", tapFunc = { switchFun.invoke(0) })
        ChatTabItem(content = "群聊", tapFunc = { switchFun.invoke(1) })
    }
}

@Composable
fun ChatTabItem(
    content: String,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    tapFunc: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(50.dp)
            .background(Color.Transparent)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                enabled = true,
                role = null,
                onClick = tapFunc
            ), contentAlignment = Alignment.Center
    ) {
        Text(content)
    }
}

@Composable
fun ChatList(options: List<SwipeActionItem>, toast: ToastState, itemClick: (() -> Unit)) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                top = 4.dp,
                bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp
            )
    ) {
        items(11) { item ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                WeSwipeAction(
                    //                    startOptions = options.slice(0..1),
                    endOptions = options,
                    style = SwipeActionStyle.ICON,
                    onStartTap = {
                        toast.show("你点击了左边的${options[it].label}")
                    },
                    onEndTap = {
                        toast.show("你点击了右边的${options[it].label}")
                    },
                    height = 80.dp,
                ) {
                    ChatListItem(itemClick)
                }
            }
        }
    }
}

@Composable
fun ChatListItem(itemClick: (() -> Unit)) {
    Surface( //        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
            .clickable { itemClick() }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(80.dp)
                    .padding(start = 1.dp)
                    .fillMaxWidth()
            ) {
                Avatar(
                    url = "https://n.sinaimg.cn/sinacn20111/600/w1920h1080/20190902/dde0-ieaiqii0290448.jpg",
                    size = 50.dp
                )
                Column(
                    modifier = Modifier
                        .padding(start = 15.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth()
                    ) {
                        Nickname("This is my name")
                        TimeWidget(sendTime = TimeUtils.getNow() - 10000)
                    }
                    ChatContent(content = "最新的消息")
                }
            }
        }
    }
}

@Composable
fun Nickname(name: String = "这是我的名字") {
    Text(name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
}

@Composable
fun TimeWidget(sendTime: Long) {
    Text(
        TimeUtils.timeToText(sendTime),
        textAlign = TextAlign.Center,
        style = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.Gray),
        modifier = Modifier.padding(top = 2.dp)
    )
}

@Composable
fun ChatContent(content: String) {
    Text(content)
}

@Composable
fun Avatar(url: String, size: Dp = 40.dp, tapFn: () -> Unit = {}) {
    Surface(shape = CircleShape) {
        Column(
            modifier = Modifier
                .size(size)
                .border(width = size / 2, color = Color.Transparent, shape = CircleShape)
                .paint(painter = rememberAsyncPainter(url), contentScale = ContentScale.Crop)
                .clickable {
                    tapFn()
                }
        ) {

        }
    }
}