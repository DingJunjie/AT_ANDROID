package com.bitat.router

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bitat.ui.Home
import com.bitat.ui.blog.BlogDetailPage
import com.bitat.ui.chat.ChatDetailsPage
import com.bitat.ui.common.GDMapPage
import com.bitat.ui.discovery.DiscoveryPage
import com.bitat.ui.login.LoginPage
import com.bitat.ui.profile.ProfilePage
import com.bitat.ui.publish.PublishDetailPage
import com.bitat.ui.publish.PublishPage
import com.bitat.ui.reel.ReelPageDemo
import com.google.accompanist.permissions.ExperimentalPermissionsApi

enum class Screen {
    LOGIN, HOME, DISCOVERY, PUBLISH, CHAT, PROFILE, PROFILE_OTHER, VIDEO, BLOG_DETAIL, PUBLISH_DETAIL, CHAT_DETAIL, REEL_PAGE_DEMO, GDMAP
}

sealed class NavigationItem(val route: String) {
    data object Login : NavigationItem(Screen.LOGIN.name)
    data object Home : NavigationItem(Screen.HOME.name)
    data object Discovery : NavigationItem(Screen.DISCOVERY.name)
    data object Publish : NavigationItem(Screen.PUBLISH.name)
    data object Chat : NavigationItem(Screen.CHAT.name)
    data object Profile : NavigationItem(Screen.PROFILE.name)
    data object ProfileOther : NavigationItem(Screen.PROFILE_OTHER.name)
    data object Video : NavigationItem(Screen.VIDEO.name)
    data object BlogDetail : NavigationItem(Screen.BLOG_DETAIL.name)
    data object PublishDetail : NavigationItem(Screen.PUBLISH_DETAIL.name)
    data object ChatDetails : NavigationItem(Screen.CHAT_DETAIL.name)
    data object ReelPageDemo : NavigationItem(Screen.REEL_PAGE_DEMO.name)
    data object GDMap : NavigationItem(Screen.GDMAP.name)
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    navigation: AtNavigation,
    startDestination: String = NavigationItem.Login.route,
    viewModelProvider: ViewModelProvider,
) {

    NavHost(navController = navController, startDestination = startDestination) {

        composable(NavigationItem.Login.route) {
            LoginPage(navController, navigation.navigateToHome)
        }

        composable(NavigationItem.Home.route) {
            Home(navController = navController, viewModelProvider)
        }

        composable(NavigationItem.BlogDetail.route) {
            BlogDetailPage(viewModelProvider)
        }

        composable(NavigationItem.Profile.route) {
            ProfilePage(navController)
        }

        composable(NavigationItem.Profile.route, arguments = listOf(navArgument("id") {
            type = NavType.IntType
        })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 1 //            ProfileOtherPage(id)
        }

        composable(NavigationItem.Discovery.route) {
            DiscoveryPage(navController)
        }

        //        composable(NavigationItem.Video.route) {
        //            VideoPage()
        //        }
        composable(NavigationItem.ReelPageDemo.route) {
            ReelPageDemo()
        }

        //        composable(NavigationItem.Video.route) {
        //            VideoPage()
        //        }

        composable(NavigationItem.Publish.route) {
            PublishPage(navController, viewModelProvider)
        }

        composable(NavigationItem.PublishDetail.route) {
            PublishDetailPage(navController, viewModelProvider)
        }

        composable(NavigationItem.ChatDetails.route) {
            ChatDetailsPage(navController)

        }

        composable(NavigationItem.GDMap.route) {
            GDMapPage()

        }
    }
}

class AtNavigation(navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(NavigationItem.Login.route) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

    val navigateToHome: () -> Unit = { // launchSingleTop = true 只保留一个在顶层
        navController.navigate(NavigationItem.Home.route) { //                val homeBaseViewModel=  hiltViewModel<HomeBaseViewModel>()
            popUpTo(0) {
                inclusive = true
            }
        }
    }

    val navigateToVideo: () -> Unit = {
        navController.navigate(NavigationItem.ReelPageDemo.route) {}
    }

    val navigateToBlogDetail: () -> Unit = {
        navController.navigate((NavigationItem.BlogDetail.route))
    }

    val navigateToPublish: () -> Unit = {
        navController.navigate((NavigationItem.Publish.route)) {

        }
    }

    val navigateToPublishDetail: () -> Unit = {

        navController.navigate((NavigationItem.PublishDetail.route)) {
//            popUpTo(0) {
//                inclusive = true
//            }
        }
    }

    val navigateToChatDetailsPage: () -> Unit = {
        navController.navigate((NavigationItem.ChatDetails.route)) {

        }
    }

    val navigateToGDMapPage: () -> Unit = {
        navController.navigate((NavigationItem.GDMap.route)) {

        }
    }


}