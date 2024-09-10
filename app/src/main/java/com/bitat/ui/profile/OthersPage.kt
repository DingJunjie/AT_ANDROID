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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLACKLIST
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.consts.PROFILE_OTHER
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.router.NavigationItem
import com.bitat.state.OTHER_TAB_OPTIONS
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.Popup
import com.bitat.ui.theme.hintTextColor
import com.bitat.utils.RelationUtils
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.BlogMoreViewModel
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.ChatViewModel
import com.bitat.viewModel.FollowBtnViewModel
import com.bitat.viewModel.OthersViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OthersPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[OthersViewModel::class]
    val state by vm.othersState.collectAsState();

    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()
    val followVm = viewModelProvider[FollowBtnViewModel::class]
    val followState by followVm.state.collectAsState()
    val blogVm = viewModelProvider[BlogViewModel::class]
    val blogState by blogVm.blogState.collectAsState()

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

    var moreVisible by remember {
        mutableStateOf(false)
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


    LaunchedEffect(Unit) {
        // 页面打开记录浏览历史


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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box {
                    state.userInfo?.let { user ->
                        ProfileBg(user.value.cover, menu = {
                            Menu(menuFun = {
                                moreVisible = true
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
                                state.userInfo!!.value, goChatDetail = {
                                    chatVm.createRoom(it)
                                    navController.navigate(NavigationItem.ChatDetails.route)
                                }, followFn = {
                                    state.userInfo?.let { user ->
                                        when (user.value.rel) {
                                            DEFAULT -> {
                                                followVm.followUser(
                                                    user.value.id,
                                                    FOLLOWED,
                                                    onSuccess = {

                                                        vm.getUserInfo() { user ->
                                                            blogState.currentBlog?.let {
                                                                it.rel = user.rel
                                                                blogVm.refreshCurrent(it)
                                                            }

                                                        }
                                                        UserStore.refreshUser()

                                                    },
                                                    onError = {})
                                            }

                                            FOLLOWED -> {
                                                followVm.followUser(
                                                    user.value.id,
                                                    DEFAULT,
                                                    onSuccess = { rel ->

                                                        UserStore.refreshUser()
                                                        vm.getUserInfo()
                                                    },
                                                    onError = {})
                                            }

                                            BLACKLIST -> {
                                                followVm.followUser(
                                                    user.value.id,
                                                    DEFAULT,
                                                    onSuccess = { rel ->

                                                        UserStore.refreshUser()
                                                        vm.getUserInfo()
                                                    },
                                                    onError = {})
                                            }
                                        }
                                    }

                                })
                        }
                    }
                }

                Column( // 获取tab bar的全局位置
                    modifier = Modifier.onGloballyPositioned { coordinate -> // 这个是获取组件的尺寸 coordinate.size
                        if (positionTopBar == 0 && coordinate.positionInRoot().y.toInt() > 0) {
                            positionTopBar =
                                coordinate.positionInRoot().y.toInt() - (padding.calculateTopPadding().value).toInt()
                        }
                    }) {
                    if (!state.isTabbarTop) Box(modifier = Modifier.fillMaxWidth()) {
                        ProfileTabBar(pagerState, OTHER_TAB_OPTIONS) { index -> vm.tabType(index) }
                    }
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
        state.userInfo?.let {
            OtherMorePop(moreVisible, it.value, navController, viewModelProvider) {
                moreVisible = false
            }
        }

    }
}

@Composable
fun OthersDetail(
    viewModel: OthersViewModel,
    navHostController: NavHostController,
    userInfo: UserPartDto,
    goChatDetail: (UserPartDto) -> Unit, followFn: () -> Unit
) {
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
                        TagLabel(TimeUtils.getAgeByBirthday(userInfo.birthday).toString())
                        TagLabel(userInfo.address)
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
                    }
                }

            }

            Text(
                userInfo.introduce,
                maxLines = 3,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            OthersOperationBar(userInfo, followFn, chatFn = { goChatDetail(userInfo) })
//            AlbumList()
        }
    }
}

@Composable
fun OthersOperationBar(userInfo: UserPartDto, followFn: () -> Unit, chatFn: () -> Unit) {
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
                        .clickable(onClick = { followFn() })
                        .background(Color.LightGray), contentAlignment = Alignment.Center
                ) {
                    Text(text = RelationUtils.toFollowContent(userInfo.rel))
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

@Composable
fun OtherMorePop(
    visible: Boolean,
    user: UserPartDto,
    navController: NavHostController,
    viewModelProvider: ViewModelProvider,
    onClose: () -> Unit
) {
    val vm = viewModelProvider[BlogMoreViewModel::class]
    val state by vm.state.collectAsState()
    LaunchedEffect(Unit) {
        vm.setUser(state.userId)
    }

    Popup(visible = visible, onClose = onClose) {
        Column(modifier = Modifier.padding(20.dp).background(MaterialTheme.colorScheme.background)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = user.nickname,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "艾特号：${user.account}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = hintTextColor
                )
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            Row {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.gray_E0))
                        .clickable(onClick = {

                            AtNavigation(navController).navigateToReportUserPage()
                        })
                ) {
                    Text( modifier = Modifier.align(Alignment.Center),text = stringResource(R.string.blog_report))

                }
                Spacer(
                    modifier = Modifier
                        .width(30.dp)
                        .height(10.dp)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.gray_E0))
                        .clickable(onClick = {

                            vm.masking()
                            onClose()
                        })
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.blog_masking)
                    )

                }
            }
        }
    }
}