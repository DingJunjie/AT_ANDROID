package com.bitat.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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

/****
 * 首页的切换
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun Home(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: HomeViewModel = viewModelProvider[HomeViewModel::class]
    val state by vm.homeState.collectAsState()


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
    }, content = { _ ->

        when (state.selectedIndex) {
            0 -> {

                BlogPage(
                    navController,
                    viewModelProvider = viewModelProvider
                ) //                AtNavigation(navController).navigateToBlog()
            }

            1 -> DiscoveryPage(navController, viewModelProvider)
            2 -> {
                //                PublishTextPage(navController)
                //                PublishPage(
                //                    navHostController = navController,
                //                    viewModelProvider = viewModelProvider
                //                )
                //                AtNavigation(navController).navigateToPublishText
                AtNavigation(navController).navigateToPublish()
            }

            3 -> ChatPage(navController)
            4 -> ProfilePage(navController, viewModelProvider)
        } //        }
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

    BottomNavigation(
        modifier = Modifier
            .onGloballyPositioned { coordinates -> // 获取高度
                val heightPx =
                    coordinates.size.height
                with(ctx) {
                    vm.setBottom(heightPx.toDp())
                }
            }
            .windowInsetsPadding(
                WindowInsets.navigationBars // 处理状态栏和导航栏
            ),
        //            .height(dimensionResource(R.dimen.home_tab_height)),
        backgroundColor = Color.White
    ) {
        tabList.forEachIndexed { index, tab ->
            BottomNavigationItem(
                modifier = Modifier.selectable(
                    selected = selectIndex == index,
                    onClick = { onTabChange(index) },
                    enabled = true,
                    role = Role.Tab,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),
                icon = {
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
                },
                selected = selectIndex == index,
                onClick = { onTabChange(index) })
        }
    }


}



