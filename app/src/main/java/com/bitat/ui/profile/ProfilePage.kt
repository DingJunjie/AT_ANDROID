package com.bitat.ui.profile

import com.bitat.repository.store.UserStore
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.clickableWithoutRipple
import com.bitat.ext.toAmountUnit
import com.bitat.ui.component.rememberDialogState
import com.bitat.state.PROFILE_TAB_OPTIONS
import com.bitat.utils.ScreenUtils
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.ui.component.LottieBox
import com.bitat.viewModel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    TextButton(
        content = { SvgIcon(path, contentDescription = desc) },
        onClick = tapFun,
        shape = RoundedCornerShape(size = 40.dp),
        modifier = Modifier.size(40.dp),
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = Color(red = 33, green = 33, blue = 33, alpha = 100),
            disabledContentColor = Color.White,
            disabledContainerColor = Color.Gray
        )
    )
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(navController: NavHostController) {
    val dialog = rememberDialogState()
    val viewModel: ProfileViewModel = viewModel()

    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val fans by remember {
        derivedStateOf {
            UserStore.userInfo.fans
        }
    }

    LaunchedEffect(Dispatchers.Default) {
        UserStore.userFlow.collect { value ->
            println(value)
        }
    }

    val userInfo by remember {
        mutableStateOf(UserStore.userInfo)
    }

    // tab bar 的state
    val pagerState: PagerState = rememberPagerState {
        PROFILE_TAB_OPTIONS.size
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
        mutableStateOf(Offset.Zero)
    }

    // scroll 的触发
    if (scrollState.value > positionTopBar.y && !state.isTabbarTop) {
        viewModel.switchTabbar(true)
    } else if (scrollState.value < positionTopBar.y && state.isTabbarTop) {
        viewModel.switchTabbar(false)
    }

    var offsetY by remember {
        mutableFloatStateOf(0f)
    }

    val draggableState = rememberDraggableState { delta ->
        offsetY += delta
    }



    ModalNavigationDrawer(drawerState = drawerState,
//        modifier = Modifier.width(100.dp),
        scrimColor = Color(0x33333333),
        drawerContent = { /*TODO*/
            DrawerContainer(scope, drawerState)
        },
        content = {
            Box(
                modifier = Modifier
                    .verticalScroll(state = scrollState)
                    .height((ScreenUtils.screenHeight * 2).dp)
            ) {
                Box(
                    modifier = Modifier.draggable(
                        orientation = Orientation.Vertical,
                        state = draggableState
                    )
                ) {
                    ProfileBg(menu = {
                        Menu(menuFun = {
                            scope.launch {
                                drawerState.open()
                            }
                        })
                    })

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieBox(
                            lottieRes = R.raw.like_ani,
                            isRepeat = true,
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight() //            .height(500.dp)
                    //            .verticalScroll(state = scrollState)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                        )
                        ProfileDetail(
                            viewModel,
                            nickname = userInfo.nickname,
                            atAccount = userInfo.account,
                            introduction = userInfo.introduce,
                            likes = userInfo.agrees,
                            follows = userInfo.follows,
                            fans = fans
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth() //                    .height(200.dp)
                        ) {
                            Column( // 获取tab bar的全局位置
                                modifier = Modifier.onGloballyPositioned { coordinate -> // 这个是获取组件的尺寸 coordinate.size
                                    if (positionTopBar.y.toInt() == 0) positionTopBar =
                                        coordinate.positionInRoot()
                                }) {
                                if (!state.isTabbarTop) Box(modifier = Modifier.fillMaxWidth()) {
                                    ProfileTabBar(pagerState, PROFILE_TAB_OPTIONS)
                                } else Box(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .background(Color.White)
                                )
                                ProfileTabView(options = PROFILE_TAB_OPTIONS, pagerState) { index ->
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) { //                        Text(
                                        //                            text = (index + 1).toString(),
                                        //                            color = MaterialTheme.colorScheme.onPrimary,
                                        //                            fontSize = 60.sp
                                        //                        )
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
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .padding(top = 180.dp)
                ) {
                    Avatar(url = "https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg")
                }

            }

            if (state.isTabbarTop) Box(modifier = Modifier.fillMaxWidth()) {
                ProfileTabBar(pagerState, PROFILE_TAB_OPTIONS)
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x45333333))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    scope.launch {
                        drawerState.close()
                    }
                }) { //            Box(
                //                modifier = Modifier
                //                    .fillMaxWidth(0.7f)
                //                    .align(Alignment.TopEnd)
                //            ) {
                //                ProfileDrawer(viewModel, drawerState, scope)
                //            }
            }


        })


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContainer(scope: CoroutineScope, drawerState: DrawerState) {
    Scaffold(modifier = Modifier
        .width((ScreenUtils.screenWidth * 0.7).dp)
        .offset(x = (ScreenUtils.screenWidth * 0).dp)
        .padding(start = 0.dp), topBar = {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Text("Main Content")
        }
    })
}

@Composable
fun ReadDataFromDatabase(
    context: Context,
    viewModel: ProfileViewModel
) { //    val data = viewModel.uiState.collectAsState()
    val data by remember {
        mutableStateOf(viewModel.uiState.value)
    }

    println(data);

    LazyColumn {
        items(count = data.userList.size) { item ->
            Row {
                Text(text = data.userList[item].name)
            }
        }
    }
}


@Composable
fun ProfileBg(menu: @Composable (() -> Unit)) {
    Surface() {
        Box(
            modifier = Modifier
                .paint(
                    painter = rememberAsyncPainter(url = "https://img.keaitupian.cn/uploads/2020/12/08/38d0befdc3c89348d6eeaed90c9b7660.jpg"),
                    contentScale = ContentScale.Crop
                )
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .height(100.dp)
            ) {
                menu.invoke();
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDrawer(viewModel: ProfileViewModel, drawerState: DrawerState, scope: CoroutineScope) {
    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = { /*TODO*/ //            Column {
            //                Text("hello")
            //                Text("world")
            //                Button(onClick = { /*TODO*/
            //                    viewModel.closeDrawer()
            //                }) {
            //                    Text("close drawer", color = Color.Black)
            //                }
            //            }
        }) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
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
fun ProfileDetail(
    viewModel: ProfileViewModel,
    nickname: String,
    atAccount: String,
    introduction: String,
    fans: Int,
    follows: Int,
    likes: Int
) {
    val options = remember {
        List(4) { "Tab ${it + 1}" }
    }

    Surface(
        shape = RoundedCornerShape(25.dp),
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                SocialData(likes, follows, fans)
                ReadDataFromDatabase(context = LocalContext.current, viewModel)
            }
            Box(modifier = Modifier.padding(start = 30.dp)) {
                UserInfo(nickname, atAccount, introduction)
            }
            AlbumList()
        }
    }
}

@Composable
fun AlbumList() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        contentPadding = PaddingValues(start = 20.dp)
    ) {
        items(5) { _ ->
            AlbumItem()
        }
    }
}

@Composable
fun AlbumItem() {
    Surface(shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
                .background(Color.Yellow)
        ) {

        }
    }
}

@Composable
fun Avatar(url: String) {

    Surface(
        shape = CircleShape, modifier = Modifier
            .padding(start = 10.dp)
            .offset(y = (-40).dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(width = 40.dp, color = Color.Transparent, shape = CircleShape)
                .paint(painter = rememberAsyncPainter(url), contentScale = ContentScale.Crop)
        ) {

        }
    }
}

@Composable
fun SocialData(likes: Int, follows: Int, fans: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(2 / 3f)
            .padding(top = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SocialDataItem(likes, "获赞")
        VerticalDivider(
            modifier = Modifier
                .height(40.dp)
                .padding(top = 10.dp),
            color = Color(0xffeeeeee)
        )
        SocialDataItem(fans, "粉丝")
        VerticalDivider(
            modifier = Modifier
                .height(40.dp)
                .padding(top = 10.dp),
            color = Color(0xffeeeeee)
        )
        SocialDataItem(follows, "关注")
    }
}

@Composable
fun SocialDataItem(amount: Int, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(text = amount.toAmountUnit(1), fontSize = 20.sp, fontWeight = FontWeight(600))
        Text(title, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun UserInfo(nickname: String, atAccount: String, introduction: String) {
    Column {
        Text(nickname, fontSize = 20.sp, fontWeight = FontWeight(600))
        Text(
            "艾特号：$atAccount",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(introduction, maxLines = 3, fontSize = 18.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileTabView(
    options: List<String>,
    pagerState: PagerState,
    content: @Composable PagerScope.(Int) -> Unit
) { //    Column {
    //        ProfileTabBar(pagerState, options)
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
        content(index)
    } //    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileTabBar(pagerState: PagerState, options: List<String>) {
    val coroutineScope = rememberCoroutineScope()

    ScrollableTabRow(selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .width(10.dp), height = 2.5.dp, color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        divider = {}) {
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
                    }
                }
                .fillMaxWidth()
                .padding(vertical = 16.dp), textAlign = TextAlign.Center
            )
        }
    }
}