package com.bitat.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bitat.MainCo
import com.bitat.repository.store.TokenStore
import com.bitat.ui.Home
import com.bitat.ui.Splash
import com.bitat.ui.blog.BlogDetailPage
import com.bitat.ui.blog.ReportUserPage
import com.bitat.ui.chat.ChatDetailsPage
import com.bitat.ui.common.GDMapPage
import com.bitat.ui.component.ImagePreviewPage
import com.bitat.ui.discovery.DiscoveryDetailPage
import com.bitat.ui.discovery.DiscoveryPage
import com.bitat.ui.discovery.SearchPage
import com.bitat.ui.discovery.SearchResultPage
import com.bitat.ui.login.LoginPage
import com.bitat.ui.profile.CollectionDetail
import com.bitat.ui.profile.FansPage
import com.bitat.ui.profile.FollowsPage
import com.bitat.ui.profile.OthersPage
import com.bitat.ui.profile.ProfilePage
import com.bitat.ui.publish.PictureDisplay
import com.bitat.ui.publish.PublishDetailPage
import com.bitat.ui.publish.PublishPage
import com.bitat.ui.publish.VideoDisplay
import com.bitat.ui.reel.ReelPageDemo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

enum class Screen {
    SPLASH, LOGIN, HOME, DISCOVERY, DISCOVERY_DETAIL, PUBLISH, CHAT, PROFILE, PROFILE_OTHER, VIDEO, BLOG_DETAIL, PUBLISH_DETAIL, CHAT_DETAIL, REEL_PAGE_DEMO, GD_MAP, PICTURE_DISPLAY, VIDEO_DISPLAY, SEARCH, SEARCH_RESULT, IMAGE_PREVIEW, REPORT_USER, BLOG, COLLECTION_DETAIL, OTHERS, FANS, FOLLOWS
}

sealed class NavigationItem(val route: String) {
    data object Splash : NavigationItem(Screen.SPLASH.name)
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
    data object GDMap : NavigationItem(Screen.GD_MAP.name)
    data object PictureDisplay : NavigationItem(Screen.PICTURE_DISPLAY.name)
    data object VideoDisplay : NavigationItem(Screen.VIDEO_DISPLAY.name)
    data object Search : NavigationItem(Screen.SEARCH.name)
    data object SearchResult : NavigationItem(Screen.SEARCH_RESULT.name)
    data object DiscoveryDetail : NavigationItem(Screen.DISCOVERY_DETAIL.name)
    data object ImagePreview : NavigationItem(Screen.IMAGE_PREVIEW.name)
    data object ReportUser : NavigationItem(Screen.REPORT_USER.name)
    data object Blog : NavigationItem(Screen.BLOG.name)
    data object CollectionDetail : NavigationItem(Screen.COLLECTION_DETAIL.name)
    data object Others : NavigationItem(Screen.OTHERS.name)
    data object Fans : NavigationItem(Screen.FANS.name)
    data object Follows : NavigationItem(Screen.FOLLOWS.name)
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    navigation: AtNavigation,
    startDestination: String = NavigationItem.Splash.route,
    viewModelProvider: ViewModelProvider,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavigationItem.Splash.route) {
            Splash(navHostController = navController)
        }

        composable(NavigationItem.Login.route) {
            LoginPage(navController, navigation.navigateToHome)
        }

        composable(NavigationItem.Home.route) {
            Home(navController = navController, viewModelProvider)
        }

        composable(NavigationItem.BlogDetail.route) {
            BlogDetailPage(navController, viewModelProvider)
        }

        composable(NavigationItem.Profile.route) {
            ProfilePage(navController, viewModelProvider)
        }

//        composable(NavigationItem.Profile.route, arguments = listOf(navArgument("id") {
//            type = NavType.IntType
//        })) { backStackEntry ->
//            val id = backStackEntry.arguments?.getInt("id") ?: 1 //            ProfileOtherPage(id)
//        }

        composable(NavigationItem.Discovery.route) {
            DiscoveryPage(navController, viewModelProvider)
        }

        //        composable(NavigationItem.Video.route) {
        //            VideoPage()
        //        }
        composable(NavigationItem.ReelPageDemo.route) {
            ReelPageDemo(navController, viewModelProvider)
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

        composable(NavigationItem.PictureDisplay.route) {
            PictureDisplay(navController, viewModelProvider)
        }

        composable(NavigationItem.VideoDisplay.route) {
            VideoDisplay(navController, viewModelProvider)
        }

        composable(NavigationItem.Search.route) {
            SearchPage(navController, viewModelProvider)
        }

        composable(NavigationItem.SearchResult.route) {
            SearchResultPage(navController, viewModelProvider)
        }

        composable(NavigationItem.DiscoveryDetail.route) {
            DiscoveryDetailPage(navController, viewModelProvider)
        }

        composable(NavigationItem.ImagePreview.route) {

            ImagePreviewPage(navController = navController, viewModelProvider)
        }
        composable(NavigationItem.ReportUser.route) {
            ReportUserPage(navHostController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.CollectionDetail.route) {
            CollectionDetail(
                navHostController = navController,
                viewModelProvider = viewModelProvider
            )
        }

        composable(NavigationItem.Others.route) {
            OthersPage(navController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.Fans.route) {
            FansPage(navHostController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.Follows.route) {
            FollowsPage(navHostController = navController, viewModelProvider = viewModelProvider)
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

//    val navigateToBlog: () -> Unit = {
//        navController.navigate(..route) {
//            popUpTo(0) {
//                inclusive = true
//            }
//        }
//    }

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

    val navigateToPublish: () -> Unit = { //         launchSingleTop = true
        navController.navigate((NavigationItem.Publish.route)) {
            launchSingleTop = true
        }
    }

    val navigateToPublishDetail: () -> Unit = {

        navController.navigate((NavigationItem.PublishDetail.route)) { //            popUpTo(0) {
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

    val navigateToImagePreviewPage: () -> Unit = {
        navController.navigate(NavigationItem.ImagePreview.route)
    }

    val navigateToReportUserPage: () -> Unit = {
        navController.navigate(NavigationItem.ReportUser.route)
    }

    val navigateToProfilePage: () -> Unit = {
        navController.navigate(NavigationItem.Profile.route)
    }


}