package com.bitat.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.clickableWithoutRipple
import com.bitat.repository.store.UserStore
import com.bitat.state.OTHER_TAB_OPTIONS
import com.bitat.ui.common.LottieBox
import com.bitat.viewModel.OthersViewModel
import com.bitat.viewModel.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OthersPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[OthersViewModel::class]
    val state by vm.othersState.collectAsState();

    val scope = rememberCoroutineScope()
    // tab bar 的state
    val pagerState: PagerState = rememberPagerState {
        OTHER_TAB_OPTIONS.size
    }

    var positionTopBar by remember {
        mutableStateOf(Offset.Zero)
    }

    var offsetY by remember {
        mutableFloatStateOf(0f)
    }

    val draggableState = rememberDraggableState { delta ->
        offsetY += delta
    }

    val scrollState: ScrollState = rememberScrollState()

    // scroll 的触发
    if (scrollState.value > positionTopBar.y && !state.isTabbarTop) {
        vm.switchTabbar(true)
    } else if (scrollState.value < positionTopBar.y && state.isTabbarTop) {
        vm.switchTabbar(false)
    }

    Box(
        modifier = Modifier
            .verticalScroll(state = scrollState)
            .background(Color.White)
            .padding(bottom = 40.dp)
    ) {
        Box(
            modifier = Modifier.draggable(
                orientation = Orientation.Vertical, state = draggableState
            )
        ) {
            ProfileBg(menu = {
                Menu(menuFun = {

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
                .fillMaxHeight()
//            .verticalScroll(state = scrollState)
        ) {
            Column(
                verticalArrangement = Arrangement.Top, modifier = Modifier.padding(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth()
                )
                if (state.userInfo != null) OthersDetail(
                    vm,
                    navController,
                    nickname = state.userInfo!!.value.nickname,
                    atAccount = state.userInfo!!.value.account,
                    introduction = state.userInfo!!.value.nickname,
                    avatar = state.userInfo!!.value.profile,
                    cover = state.userInfo!!.value.cover,
                    likes = state.userInfo!!.value.agrees,
                    follows = state.userInfo!!.value.follows,
                    fans = state.userInfo!!.value.fans
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
                            ProfileTabBar(pagerState, OTHER_TAB_OPTIONS)
                        } else Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .background(Color.White)
                        )
                        ProfileTabView(
                            options = OTHER_TAB_OPTIONS,
                            pagerState,
                            navController,
                            viewModelProvider = viewModelProvider,
                        ) { index ->
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
    }
}

@Composable
fun OthersDetail(
    viewModel: OthersViewModel,
    navHostController: NavHostController,
    nickname: String,
    atAccount: String,
    introduction: String,
    avatar: String,
    cover: String,
    fans: Int,
    follows: Int,
    likes: Int
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
                AvatarWithShadow(url = avatar)

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
                        UserInfo(nickname, atAccount, introduction)
                        SocialData(likes, fans = fans)
//                        ReadDataFromDatabase(context = LocalContext.current, viewModel)
                    }
                }

            }
//            Box(modifier = Modifier.padding(start = 30.dp)) {
//                UserInfo(nickname, atAccount, introduction)
//            }
            Text(
                introduction,
                maxLines = 3,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            OthersOperationBar()
//            AlbumList()
        }
    }
}

@Composable
fun OthersOperationBar() {
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