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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.MainCo
import com.bitat.ext.toChatMessage
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.singleChat.GetNewMessage
import com.bitat.repository.singleChat.GetRooms
import com.bitat.repository.singleChat.SetMute
import com.bitat.repository.singleChat.SetTop
import com.bitat.repository.singleChat.SingleChatHelper
import com.bitat.repository.singleChat.SingleChatHelper.singleChatUiFlow
import com.bitat.ui.common.ToastState
import com.bitat.ui.common.rememberToastState
import com.bitat.router.AtNavigation
import com.bitat.router.NavigationItem
import com.bitat.ui.common.SwipeActionItem
import com.bitat.ui.common.SwipeActionStyle
import com.bitat.ui.common.SwipeActionType
import com.bitat.ui.common.WeSwipeAction
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.viewModel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val tabPager: PagerState = rememberPagerState(0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()
    LaunchedEffect(Dispatchers.Default) {
        singleChatUiFlow.collect {
            when (it) {
                is GetRooms -> {
                    chatVm.updateRooms(it.rooms)
                    CuLog.info(CuTag.SingleChat, it.toString())
                }

                is SetTop -> {
                    chatVm.setTop(it.otherId, it.isTop == 1)
                }

                is SetMute -> TODO()
                is GetNewMessage -> {
                    chatVm.updateRoomContent(it.msg)
                }
            }
        }
    }


    val options = remember {
        listOf(
            SwipeActionItem(
                type = SwipeActionType.PLAIN, label = "喜欢", icon = Icons.Outlined.FavoriteBorder
            ), SwipeActionItem(
                type = SwipeActionType.WARNING, label = "收藏", icon = Icons.Outlined.Star
            ), SwipeActionItem(
                type = SwipeActionType.DANGER, label = "删除", icon = Icons.Outlined.Delete
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
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navHostController.navigate(NavigationItem.Notification.route) }) {
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
                    ChatList(chatState.chatList, options, toast, setTop = {
                        MainCo.launch {
                            SingleChatHelper.setTop(it.otherId, it.top)
                        }
                    }, setMute = {
//                            chatVm.muteRoom(it.otherId)
//                        chatState.flag = !chatState.flag
                    }, delete = {
                        chatVm.deleteRoom(it.otherId)
//                        chatState.flag = !chatState.flag
                    }, itemClick = {
                        chatVm.chooseRoom(it)
                        AtNavigation(navHostController).navigateToChatDetailsPage()
                    }, flag = chatState.flag)
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
fun ChatList(
    roomList: List<SingleRoomPo>,
    options: List<SwipeActionItem>,
    toast: ToastState,
    setTop: (SingleRoomPo) -> Unit,
    setMute: (SingleRoomPo) -> Unit,
    delete: (SingleRoomPo) -> Unit,
    itemClick: ((SingleRoomPo) -> Unit),
    flag: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                top = 4.dp,
                bottom = 30.dp
//                bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp
            ),
    ) {
        items(roomList) { item ->
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
//                    onStartTap = {
//                        setTop(item)
//                        toast.show("你点击了左边的${options[it].label}")
//                    },
                    onEndTap = {
                        when (it) {
                            0 -> {
                                item.top = abs(item.top - 1)
                                setTop(item)
                            }

                            1 -> setMute(item)
                            2 -> delete(item)
                        }
                        toast.show("你点击了右边的${options[it].label}")
                    },
                    height = 80.dp,
                ) {
                    ChatListItem(item, flag = flag, itemClick)
                }

                if (flag < 0) Text("")
            }
        }
    }
}

@Composable
fun ChatListItem(info: SingleRoomPo, flag: Int, itemClick: ((SingleRoomPo) -> Unit)) {
    Surface( //        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 10.dp)
//            .background(if (info.top > 0) Color.LightGray else Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(if (info.top == 1) Color.LightGray else Color.White)
            .clickable { itemClick(info) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(80.dp)
                    .padding(start = 1.dp)
                    .fillMaxWidth()
            ) {
                Avatar(
                    url = info.profile, size = 50.dp
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
                        Nickname(info.alias.ifEmpty { info.nickname })
                        TimeWidget(sendTime = info.time)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth()
                    ) {
                        ChatContent(content = info.content.toChatMessage(info.kind))
                        Surface(
                            modifier = Modifier.size(26.dp), color = Color.Red, shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    info.unreads.toString(),
                                    style = TextStyle(fontSize = 12.sp, color = Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (flag < 0) Text("a", color = Color.Transparent) else Text("b", color = Color.Transparent)
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
fun Avatar(url: String, modifier: Modifier = Modifier, size: Dp = 40.dp, tapFn: () -> Unit = {}) {
    Surface(shape = CircleShape) {
        Column(modifier = modifier
            .size(size)
            .border(width = size / 2, color = Color.Transparent, shape = CircleShape)
            .paint(painter = rememberAsyncPainter(url), contentScale = ContentScale.Crop)
            .clickable {
                tapFn()
            }) {

        }
    }
}