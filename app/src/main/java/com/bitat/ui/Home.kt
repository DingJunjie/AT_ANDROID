package com.bitat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.config.HomeTabCfg
import com.bitat.ext.cdp
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.BlogPage
import com.bitat.ui.chat.ChatPage
import com.bitat.ui.discovery.DiscoveryPage
import com.bitat.ui.profile.ProfilePage
import com.bitat.ui.publish.PublishDetailPage
import com.bitat.viewModel.HomeViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

/****
 * 首页的切换
 */
@Composable
fun Home(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val viewModel: HomeViewModel = viewModel()
    val tabList = listOf(HomeTabCfg.Home,
        HomeTabCfg.Discovery,
        HomeTabCfg.Add,
        HomeTabCfg.Chat,
        HomeTabCfg.Mine)

    var selectIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomAppBar(modifier = Modifier,
            contentColor = Color.White,
            containerColor = Color.White,
            actions = {
                TabRow(selectedTabIndex = 0, indicator = {}) {
                    tabList.forEachIndexed { index, tab -> //
                        IconButton(onClick = {
                            selectIndex = index
                            if (selectIndex == 2) { //                                AtNavigation(navController).navigateToPublishDetail()
                                AtNavigation(navController).navigateToPublish()
                            }
                        }) {
                            Icon(if (index == selectIndex) painterResource(tab.iconSelect) else painterResource(
                                id = tab.iconUnselect),
                                contentDescription = "",
                                modifier = Modifier.size(when (index) {
                                    2 -> 60.cdp
                                    1 -> 50.cdp
                                    else -> 40.cdp
                                }))
                        }
                    }
                }
            })

    }) { innerPadding ->
        when (selectIndex) {
            0 -> {
                BlogPage(Modifier.padding(innerPadding),
                    navController,
                    viewModelProvider = viewModelProvider)
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
            4 -> ProfilePage(navController)
        }
    }

}
