package com.bitat.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.PROFILE_OTHER
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.state.OTHER_TAB_OPTIONS
import com.bitat.ui.common.statusBarHeight
import com.bitat.viewModel.ChatViewModel
import com.bitat.viewModel.OthersViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OthersPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[OthersViewModel::class]
    val state by vm.othersState.collectAsState();

    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()

    val scope = rememberCoroutineScope() // tab bar 的state
    val pagerState: PagerState = rememberPagerState {
        OTHER_TAB_OPTIONS.size
    }

    // top tab bar 开始出现位置
    var positionTopBar by remember {
        mutableStateOf(0)
    }

    var offsetY by remember {
        mutableFloatStateOf(0f)
    }

    val draggableState = rememberDraggableState { delta ->
        offsetY += delta
    }
    // 整体 scroll 的state
    val scrollState: ScrollState = rememberScrollState()
    // scroll 的触发
    if (scrollState.value > positionTopBar && !state.isTabbarTop) {
        vm.switchTabbar(true)
    } else if (scrollState.value < positionTopBar && state.isTabbarTop) {
        vm.switchTabbar(false)
    }

    // 监听滚动状态
    LaunchedEffect(scrollState.value) {
        val maxScroll = scrollState.maxValue
        vm.atBottom(scrollState.value == maxScroll)
    }
    Scaffold { padding ->

        Column(modifier = Modifier.background(Color.White)) {

            if (state.isTabbarTop) Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(statusBarHeight)
                        .background(Color.White)
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {

                    ProfileTabBar(pagerState, OTHER_TAB_OPTIONS) { index -> vm.tabType(index) }
                }
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)) {
                Box {
                    state.userInfo?.let {  user ->
                        ProfileBg(user.value.cover,menu = {
                            Menu(menuFun = {

                            })
                        })
                        Column(modifier = Modifier.align(Alignment.TopCenter)) {
                            Box(
                                modifier = Modifier
                                    .height(160.dp)
                                    .fillMaxWidth()
                            )
                         OthersDetail(
                                vm,
                                navController,
                                state.userInfo!!.value
                            ) {
                                chatVm.createRoom(it)
                                navController.navigate(NavigationItem.ChatDetails.route)
                            }
                        }
                    }
                }

                Column( // 获取tab bar的全局位置
                    modifier = Modifier.onGloballyPositioned { coordinate -> // 这个是获取组件的尺寸 coordinate.size
                        if (positionTopBar == 0) positionTopBar =
                            coordinate.positionInRoot().y.toInt() - (padding.calculateTopPadding().value).toInt()
                    }) {
                    if (!state.isTabbarTop) Box(modifier = Modifier.fillMaxWidth()) {
                        ProfileTabBar(pagerState, OTHER_TAB_OPTIONS) { index -> vm.tabType(index) }
                    } else Box(
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .background(Color.White)
                    )
                    ProfileTabView(
                        type = PROFILE_OTHER, userId = state.userId,
                        options = OTHER_TAB_OPTIONS,
                        pagerState,
                        navController,
                        viewModelProvider = viewModelProvider,
                    ) { index ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier
                                .fillMaxHeight()
                                .background(Color.Cyan)
                                .fillMaxWidth()
                                .clickable {
                                    UserStore.updateFans(100)
                                }) {

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OthersDetail(
    viewModel: OthersViewModel,
    navHostController: NavHostController,
    userInfo: UserPartDto,
    goChatDetail: (UserPartDto) -> Unit
) {
    val options = remember {
        List(4) { "Tab ${it + 1}" }
    }

    Surface(
        shape = RoundedCornerShape(40.dp, 40.dp, 0.dp, 0.dp),
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AvatarWithShadow(url = userInfo.profile)

                Column(
                    modifier = Modifier
                        .padding(top = 15.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.padding(bottom = 5.dp, start = 5.dp)) {
                        TagLabel("28")
                        TagLabel(
                            "贵阳"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                            .height(55.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserInfo(userInfo.nickname, userInfo.account, userInfo.introduce)
                        SocialData(userInfo.agrees, fans = userInfo.fans)
//                        ReadDataFromDatabase(context = LocalContext.current, viewModel)
                    }
                }

            }
//            Box(modifier = Modifier.padding(start = 30.dp)) {
//                UserInfo(nickname, atAccount, introduction)
//            }
            Text(
                userInfo.introduce,
                maxLines = 3,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            OthersOperationBar(followFn = {}, chatFn = { goChatDetail(userInfo) })
//            AlbumList()
        }
    }
}

@Composable
fun OthersOperationBar(followFn: () -> Unit, chatFn: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(50.dp)) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "")
        }

        Row(
            modifier = Modifier
                .weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp), modifier = Modifier
                    .weight(0.5f)
                    .height(40.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray), contentAlignment = Alignment.Center
                ) {
                    Text(text = "已关注")
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp), modifier = Modifier
                    .weight(0.5f)
                    .height(40.dp)
                    .padding(horizontal = 10.dp)
                    .clickable {
                        chatFn()
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray), contentAlignment = Alignment.Center
                ) {
                    Text(text = "私信")
                }
            }
        }
    }
}