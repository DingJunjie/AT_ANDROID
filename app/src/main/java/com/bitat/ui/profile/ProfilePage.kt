package com.bitat.ui.profile

import android.content.Context
import android.view.WindowInsets
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.PROFILE_MINE
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.state.PROFILE_TAB_OPTIONS
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.ui.common.rememberDialogState
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.ui.common.LottieBox
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.Popup
import com.bitat.ui.theme.Typography
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.ProfileViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun Menu(menuFun: () -> Unit) {
    Row(modifier = Modifier.width(150.dp), horizontalArrangement = Arrangement.SpaceAround) {
        MenuItem(path = "svg/add.svg", tapFun = {})
        MenuItem(path = "svg/add-large-line.svg", tapFun = {})
        MenuItem(path = "svg/menu_line.svg", tapFun = menuFun)
    }
}

@Composable
fun MenuItem(path: String, desc: String = "", tapFun: () -> Unit) {
    TextButton(content = { SvgIcon(path, contentDescription = desc) },
        onClick = tapFun,
        shape = RoundedCornerShape(size = 40.dp),
        modifier = Modifier.size(40.dp),
        colors = ButtonColors(contentColor = Color.White,
            containerColor = Color(red = 33, green = 33, blue = 33, alpha = 100),
            disabledContentColor = Color.White,
            disabledContainerColor = Color.Gray))
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfilePage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val dialog = rememberDialogState()
    val vm = viewModelProvider[ProfileViewModel::class]

    val state by vm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val fans by remember {
        derivedStateOf {
            UserStore.userInfo.fans
        }
    }

    LaunchedEffect(Dispatchers.Default) {
        UserStore.userFlow.collect { value ->
            CuLog.debug(CuTag.Profile, value.nickname)
        }
    }

    val userInfo by remember {
        mutableStateOf(UserStore.userInfo)
    }

    // tab bar 的state
    val pagerState: PagerState = rememberPagerState(initialPage = state.currentTabIndex) {
        PROFILE_TAB_OPTIONS.size
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.currentTabIndex) {
            vm.updateTabIndex(pagerState.currentPage)
        }
    }

    // 整体 scroll 的state
    val scrollState: ScrollState = rememberScrollState()
    val endReached by remember {
        derivedStateOf {
            scrollState.value == scrollState.maxValue - 10
        }
    }


    // top tab bar 开始出现位置
    var positionTopBar by remember {
        mutableStateOf(0)
    }

    // scroll 的触发
    if (scrollState.value > positionTopBar && !state.isTabbarTop) {
        vm.switchTabbar(true)
    } else if (scrollState.value < positionTopBar && state.isTabbarTop) {
        vm.switchTabbar(false)
    }

    var offsetY by remember {
        mutableFloatStateOf(0f)
    }

    val draggableState = rememberDraggableState { delta ->
        offsetY += delta
    }

    // 监听滚动状态
    LaunchedEffect(scrollState.value) {
        val maxScroll = scrollState.maxValue
        vm.atBottom(scrollState.value == maxScroll)
    }


    val showBGPopup = remember {
        mutableStateOf(false)
    }

    val showDrawer = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        UserStore.userFlow.collect { user -> // 用户信息变更
            vm.updateUser(user)
        }
    }


    val drawerOffset =
        animateIntAsState(targetValue = if (showDrawer.value) (ScreenUtils.screenWidth.times(0.4)).toInt() else ScreenUtils.screenWidth)

    Scaffold { padding ->
        Column(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {

            if (state.isTabbarTop) Column(modifier = Modifier.fillMaxWidth()
                .padding(padding.calculateBottomPadding())) {
                Spacer(modifier = Modifier.fillMaxWidth().height(statusBarHeight)
                    .background(Color.White))
                Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    ProfileTabBar(pagerState, PROFILE_TAB_OPTIONS) { index ->
                        vm.tabType(index)
                    }
                }
            }

            Column(modifier = Modifier.verticalScroll(state = scrollState)
                .padding(if (!state.isTabbarTop) {
                    PaddingValues(top = 0.dp)
                } else PaddingValues(top = 5.dp)) //                    .align(Alignment.TopCenter)
            ) {

                Box(modifier = Modifier.fillMaxWidth() //                        .verticalScroll(state = scrollState)
                    .padding(bottom = androidx.compose.foundation.layout.WindowInsets.navigationBars.getBottom(
                        LocalDensity.current).dp).padding(bottom = padding.calculateBottomPadding())
                    .fillMaxHeight()) {
                    Box(modifier = Modifier.background(Color.White)

                    ) {
                        ProfileBg(tapBG = {
                            showBGPopup.value = true
                        }, menu = {
                            Menu(menuFun = {
                                scope.launch {
                                    showDrawer.value = true
                                }
                            })
                        })

                    }
                    Box(modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight() //            .height(500.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.Top,
                            modifier = Modifier.padding(0.dp)) {
                            Box(modifier = Modifier.height(160.dp).fillMaxWidth())
                            ProfileDetail(vm,
                                navController,
                                userInfo = state.user,
                                atAccount = userInfo.account,
                                introduction = userInfo.introduce,
                                likes = userInfo.agrees,
                                follows = userInfo.follows,
                                fans = fans)
                        }

                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Column( // 获取tab bar的全局位置
                        modifier = Modifier.onGloballyPositioned { coordinate -> // 这个是获取组件的尺寸 coordinate.size
                            if (positionTopBar == 0) positionTopBar =
                                coordinate.positionInRoot().y.toInt() - (padding.calculateTopPadding().value).toInt()
                        }) {
                        if (!state.isTabbarTop) Box(modifier = Modifier.fillMaxWidth()) {
                            ProfileTabBar(pagerState, PROFILE_TAB_OPTIONS) { index ->
                                vm.tabType(index)
                            }
                        }
                        else Box(modifier = Modifier.height(50.dp).fillMaxWidth()
                            .background(Color.White))

                    }
                }


                ProfileTabView(type = PROFILE_MINE,
                    userId = state.user.id,
                    options = PROFILE_TAB_OPTIONS,
                    pagerState,
                    navController,
                    viewModelProvider = viewModelProvider,
                    content = { index ->
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {

                            Box(modifier = Modifier.fillMaxHeight().background(Color.Cyan)
                                .fillMaxWidth().clickable {
                                    UserStore.updateFans(100)
                                }) {

                            }
                        }
                    })

            }
        }
    }



    if (showDrawer.value)

        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().clickable {
                showDrawer.value = false
            }.background(Color(0x33333333))) {}
            Box(
                //            contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()
                    .offset(x = drawerOffset.value.dp).clickable { }.background(Color.White),
            ) {
                Text("Main Content")
            }
        }

    BackgroundPopup(visible = showBGPopup.value, { showBGPopup.value = false }, {
        UserStore.updateCover(it)
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContainer(scope: CoroutineScope, drawerState: DrawerState) {
    Scaffold(modifier = Modifier.width((ScreenUtils.screenWidth * 0.7).dp).padding(start = 0.dp),
        topBar = {
            TopAppBar(title = { Text("Drawer Example") }, navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = null)
                }
            })
        },
        content = {
            Box(modifier = Modifier.fillMaxSize().padding(it),
                contentAlignment = Alignment.Center) {
                Text("Main Content")
            }
        })
}

@Composable
fun ReadDataFromDatabase(context: Context, viewModel: ProfileViewModel) { //    val data = viewModel.uiState.collectAsState()
    val data by remember {
        mutableStateOf(viewModel.uiState.value)
    }

    LazyColumn {
        items(count = data.userList.size) { item ->
            Row {
                Text(text = data.userList[item].name)
            }
        }
    }
}

@Composable
fun BackgroundPopup(visible: Boolean, onClose: () -> Unit, changeFn: (uri: Uri) -> Unit) {
    Popup(visible = visible, onClose = { onClose() }) {
        Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
            ImagePicker(maxSize = 1, option = ImagePickerOption.ImageOnly, onSelected = {
                changeFn(it.first())
            }) {
                Text(text = "更换背景")
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(50.dp).clickable { onClose() }) {
            Text(text = "取消")
        }
    }
}

@Composable
fun ProfileBg(tapBG: () -> Unit = {}, menu: @Composable (() -> Unit)) {
    Box(modifier = Modifier.paint(painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
        LocalContext.current).data(UserStore.userInfo.cover)
        .placeholder(R.mipmap.ic_launcher)  // 加载中的占位图
        .error(R.mipmap.ic_launcher)  // 加载失败时的默认图片
        .build()), contentScale = ContentScale.Crop).fillMaxWidth().height(200.dp)
        .clickable { tapBG() }.padding(top = 30.dp), contentAlignment = Alignment.TopEnd) {
        Box(modifier = Modifier.padding(top = 10.dp, end = 10.dp).height(200.dp)) {
            menu.invoke();
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDrawer(viewModel: ProfileViewModel, drawerState: DrawerState, scope: CoroutineScope) {
    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {}) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Drawer Example") }, navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = null)
                }
            })
        }, content = {
            Box(modifier = Modifier.fillMaxSize().padding(it),
                contentAlignment = Alignment.Center) {
                Text("Main Content")
            }
        }) //        ModalDrawerSheet {
        //            Text("设置")
        //            HorizontalDivider()
        //            NavigationDrawerItem(label = { /*TODO*/ }, selected = false, onClick = { /*TODO*/ })
        //        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileDetail(viewModel: ProfileViewModel, navHostController: NavHostController, userInfo: UserDto, atAccount: String, introduction: String, fans: Int, follows: Int, likes: Int) {
    val options = remember {
        List(4) { "Tab ${it + 1}" }
    }
    Surface(
        shape = RoundedCornerShape(40.dp),
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                AvatarWithShadow(url = UserStore.userInfo.profile)

                Column(modifier = Modifier.padding(top = 15.dp),
                    verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.padding(bottom = 5.dp, start = 5.dp)) {
                        TagLabel(TimeUtils.getAgeByBirthday(userInfo.birthday).toString())
                        TagLabel("贵阳")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(end = 10.dp).height(55.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        UserInfo(userInfo.nickname, atAccount, introduction)
                        SocialData(likes, follows, fans, tapLike = {

                        }, tapFollows = {
                            viewModel.getMyFollows()
                            navHostController.navigate(NavigationItem.Follows.route)
                        }, tapFans = {
                            viewModel.getMyFans()
                            navHostController.navigate(NavigationItem.Fans.route)
                        })
                    }
                }
            }

            Text(introduction,
                maxLines = 3,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp).clickable {
                    navHostController.navigate(NavigationItem.ProfileEdit.route)
                })
            GoCreate()
        }
    }
}

@Composable
fun GoCreate() {
    Column(modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth()) {
        Text("进入创作页 》",
            style = Typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 20.dp))
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(3) {
                Creation()
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun Creation() {
    Surface(shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(start = 15.dp, end = 5.dp)) {
        Box(modifier = Modifier.size(140.dp).background(Color.Cyan)) {}
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TagLabel(content: String) {
    Chip(onClick = { /*TODO*/ },
        modifier = Modifier.height(20.dp).padding(horizontal = 3.dp),
        colors = ChipDefaults.chipColors(backgroundColor = Color.Black),
        leadingIcon = {
            Icon(Icons.Filled.CheckCircle,
                contentDescription = "",
                Modifier.size(12.dp),
                tint = Color.White)
        }) {
        Text(
            content,
            style = Typography.bodySmall.copy(fontSize = 12.sp, color = Color.White),
        )
    }
}

@Composable
fun AlbumList() {
    LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
        contentPadding = PaddingValues(start = 20.dp)) {
        items(5) { _ ->
            AlbumItem()
        }
    }
}

@Composable
fun AlbumItem() {
    Surface(shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
        Box(modifier = Modifier.width(80.dp).height(80.dp).background(Color.Yellow)) {

        }
    }
}


@Composable
fun UserInfo(nickname: String, atAccount: String, introduction: String) {
    Column(modifier = Modifier.padding(start = 10.dp, top = 5.dp).height(55.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
        Text(nickname, fontSize = 16.sp, fontWeight = FontWeight(600))
        Text("艾特号：$atAccount",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 3.dp))
    }
}

@Composable
fun ProfileHead() {

}








