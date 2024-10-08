package com.bitat.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.config.HomeTabCfg
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.BlogPage
import com.bitat.ui.chat.ChatPage
import com.bitat.ui.discovery.DiscoveryPage
import com.bitat.ui.profile.ProfilePage
import com.bitat.viewModel.HomeViewModel
import com.bitat.viewModel.UnreadViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/****
 * 首页的切换
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun Home(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: HomeViewModel = viewModelProvider[HomeViewModel::class]
    val state by vm.homeState.collectAsState()

    val unreadVm = viewModelProvider[UnreadViewModel::class]


    val isFirstFetch = remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        if (isFirstFetch.value) {
            unreadVm.checkUnreadMessage()
            isFirstFetch.value = false
        }
    }
    //    var selectIndex by remember {
    //        mutableIntStateOf(0)
    //    }
    //     var isBack by remember { mutableStateOf(false) }


    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(lifecycleOwner) {
        val lifeCycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    CuLog.info(CuTag.Blog, "Home------------->>>> ON_STOP")
                }

                Lifecycle.Event.ON_START -> {
                    CuLog.info(CuTag.Blog, "Home------------->>>> ON_START")

                }

                Lifecycle.Event.ON_PAUSE -> {
                    CuLog.info(CuTag.Blog, "Home------------->>>> ON_PAUSE")

                }

                Lifecycle.Event.ON_RESUME -> {
                    CuLog.info(CuTag.Blog, "Home------------->>>> ON_PAUSE")

                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifeCycleObserver)
        onDispose { //            isBack=true
            CuLog.info(CuTag.Blog, "Home------------->>>> onDispose")
            lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomAppBarBar(selectIndex = state.selectedIndex, vm = vm) {
            if (it == 2) { //                                AtNavigation(navController).navigateToPublishDetail()
                AtNavigation(navController).navigateToPublish()
            } else {
                vm.setIndex(it)
            }
        }
    }, content = { padding ->
        Column(modifier = Modifier.padding(bottom = 56.dp)) {
            when (state.selectedIndex) {
                0 -> {
                    BlogPage(
                        navController,
                        viewModelProvider = viewModelProvider
                    ) //                AtNavigation(navController).navigateToBlog()
                }

                1 -> DiscoveryPage(navController, viewModelProvider)
                2 -> { //                PublishTextPage(navController)
                    //                PublishPage(
                    //                    navHostController = navController,
                    //                    viewModelProvider = viewModelProvider
                    //                )
                    //                AtNavigation(navController).navigateToPublishText
                    AtNavigation(navController).navigateToPublish()
                }

                3 -> ChatPage(navController, viewModelProvider)
                4 -> ProfilePage(navController, viewModelProvider)
            } //        }
        }
    })
}

@Composable
fun BottomAppBarBar(selectIndex: Int, vm: HomeViewModel, onTabChange: (Int) -> Unit) {
    val tabList = listOf(
        HomeTabCfg.Home,
        HomeTabCfg.Discovery,
        HomeTabCfg.Add,
        HomeTabCfg.Chat,
        HomeTabCfg.Mine
    )

    val ctx = LocalDensity.current
    Column() {
        BottomNavigation(
            modifier = Modifier
                .height(56.dp)
//                .windowInsetsPadding(
//                    WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
//                )
                .onGloballyPositioned { coordinates -> // 获取高度
                    val heightPx =
                        coordinates.size.height
                    val bottomHeight = with(ctx) {
                        vm.setBottom(heightPx.toDp())
                    }
                },
            //            .height(dimensionResource(R.dimen.home_tab_height)),
            backgroundColor = MaterialTheme.colorScheme.background
        ) {
            tabList.forEachIndexed { index, tab ->
                BottomNavigationItem(modifier = Modifier.selectable(
                    selected = selectIndex == index,
                    onClick = { onTabChange(index) },
                    enabled = true,
                    role = Role.Tab,
                    indication = null,
                    interactionSource = remember { BitMutableInteractionSourceImpl() },
                ), icon = {
                    Icon(
                        painter = if (index == selectIndex) painterResource(tab.iconSelect) else painterResource(
                            id = tab.iconUnselect
                        ),
                        contentDescription = "tabIcon",
                        modifier = Modifier.size(
                            when (index) {
                                2 -> 100.cdp
                                1 -> 40.cdp
                                else -> 40.cdp
                            }
                        ),
                        tint = Color.Unspecified
                    )
                }, selected = selectIndex == index, onClick = { onTabChange(index) })
            }
        }

        val navigationBarHeight =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(navigationBarHeight)
                .background(MaterialTheme.colorScheme.background)
        )
    }

}

@Stable
private class BitMutableInteractionSourceImpl : MutableInteractionSource {
    // TODO: consider replay for new indication instances during events?
    override val interactions = MutableSharedFlow<Interaction>(

    )

    override suspend fun emit(interaction: Interaction) {
        interactions.emit(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        return interactions.tryEmit(interaction)
    }
}



