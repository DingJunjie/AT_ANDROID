package com.bitat.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
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
        onDispose {
//            isBack=true
            CuLog.info(CuTag.Blog, "Home------------->>>> onDispose")
            lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomAppBarBar() {
            vm.setIndex(it)
            if (state.selectedIndex == 2) { //                                AtNavigation(navController).navigateToPublishDetail()
                AtNavigation(navController).navigateToPublish()
            }
        }
    }, content = { _ ->
//        if (!isBack){
        when (state.selectedIndex) {

            0 -> {
                BlogPage(
                    navController,
                    viewModelProvider = viewModelProvider
                )

                //                AtNavigation(navController).navigateToBlog()
            }

            1 -> DiscoveryPage(navController, viewModelProvider)
            2 -> { //                PublishTextPage(navController)
                //                PublishPage(
                //                    navHostController = navController,
                //                    viewModelProvider = viewModelProvider
                //                )
                //                AtNavigation(navController).navigateToPublishText
            }

            3 -> ChatPage(navController)
            4 -> ProfilePage(navController, viewModelProvider)
        }
//        }
    })

}

@Composable
fun BottomAppBarBar(onTabChange: (Int) -> Unit) {
    val tabList = listOf(
        HomeTabCfg.Home,
        HomeTabCfg.Discovery,
        HomeTabCfg.Add,
        HomeTabCfg.Chat,
        HomeTabCfg.Mine
    )

    var selectIndex by remember {
        mutableIntStateOf(0)
    }

    BottomNavigation(backgroundColor = Color.White) {
        tabList.forEachIndexed { index, tab ->
            BottomNavigationItem(icon = {
                Icon(
                    painter = if (index == selectIndex) painterResource(tab.iconSelect) else painterResource(
                        id = tab.iconUnselect
                    ),
                    contentDescription = "tabIcon",
                    modifier = Modifier.size(
                        when (index) {
                            2 -> 50.cdp
                            1 -> 50.cdp
                            else -> 50.cdp
                        }
                    )
                )
            }, selected = selectIndex == index, onClick = {
                selectIndex = index
                onTabChange(index)
            })
        }
    }
}
