package com.bitat.router

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.amap.api.services.route.Navi
import com.bitat.AppConfig.APP_CURRENT_PAGE
import com.bitat.ui.Home
import com.bitat.ui.Splash
import com.bitat.ui.blog.BlogDetailPage
import com.bitat.ui.blog.ReportUserPage
import com.bitat.ui.chat.ChatDetailsPage
import com.bitat.ui.chat.ChatHistoryPage
import com.bitat.ui.chat.ChatSettingsPage
import com.bitat.ui.chat.ContactPage
import com.bitat.ui.chat.NotificationPage
import com.bitat.ui.common.GDMapPage
import com.bitat.ui.component.ImagePreviewPage
import com.bitat.ui.discovery.DiscoveryDetailPage
import com.bitat.ui.discovery.DiscoveryPage
import com.bitat.ui.discovery.SearchPage
import com.bitat.ui.discovery.SearchResultPage
import com.bitat.ui.login.LoginPage
import com.bitat.ui.profile.AccountSecurePage
import com.bitat.ui.profile.BlackListPage
import com.bitat.ui.profile.BrowserHistoryPage
import com.bitat.ui.profile.CancelAgreementPage
import com.bitat.ui.profile.ClearCachPage
import com.bitat.ui.profile.CollectionDetail
import com.bitat.ui.profile.FansPage
import com.bitat.ui.profile.FeedbackPage
import com.bitat.ui.profile.FollowsPage
import com.bitat.ui.profile.OthersPage
import com.bitat.ui.profile.PrivacySettingsPage
import com.bitat.ui.profile.ProfileEditPage
import com.bitat.ui.profile.ProfilePage
import com.bitat.ui.profile.QRScannerPage
import com.bitat.ui.profile.SettingPage
import com.bitat.ui.profile.SignoutPage
import com.bitat.ui.publish.PictureDisplay
import com.bitat.ui.publish.PublishDetailPage
import com.bitat.ui.publish.PublishPage
import com.bitat.ui.publish.VideoDisplay
import com.bitat.ui.reel.ReelPageDemo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.serialization.Serializable

enum class Screen {
    SPLASH, LOGIN, HOME, DISCOVERY, DISCOVERY_DETAIL, PUBLISH, CHAT, PROFILE, PROFILE_OTHER, VIDEO, //
    BLOG_DETAIL, PUBLISH_DETAIL, CHAT_DETAIL, REEL_PAGE_DEMO, GD_MAP, PICTURE_DISPLAY, VIDEO_DISPLAY, //
    SEARCH, SEARCH_RESULT, IMAGE_PREVIEW, REPORT_USER, BLOG, COLLECTION_DETAIL, OTHERS, FANS, FOLLOWS, //
    PROFILE_EDIT, SETTING, CHAT_SETTINGS, CACHE, ACCOUNTSECURE, NOTIFICATION, SIGNOUT, CANCELAGREEMENT, //
    FEEDBACK, CHAT_HISTORY, BROWSHISTORY, PRIVACYSETTINGS, BLACKLIST, QRSCANNER, CONTACT
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
    data object Fans : NavigationItem(Screen.FANS.name)
    data object Follows : NavigationItem(Screen.FOLLOWS.name)
    data object ProfileEdit : NavigationItem(Screen.PROFILE_EDIT.name)
    data object Setting : NavigationItem(Screen.SETTING.name)
    data object ChatSettings : NavigationItem(Screen.CHAT_SETTINGS.name)
    data object Cache : NavigationItem(Screen.CACHE.name)
    data object AccountSecure : NavigationItem(Screen.ACCOUNTSECURE.name)
    data object Notification : NavigationItem(Screen.NOTIFICATION.name)
    data object Signout : NavigationItem(Screen.SIGNOUT.name)
    data object CancelAgreemet : NavigationItem(Screen.CANCELAGREEMENT.name)
    data object FeedBack : NavigationItem(Screen.FEEDBACK.name)
    data object BrowHistory : NavigationItem(Screen.BROWSHISTORY.name)
    data object ChatHistory : NavigationItem(Screen.CHAT_HISTORY.name)
    data object PrivacySettings : NavigationItem(Screen.PRIVACYSETTINGS.name)
    data object BlackList : NavigationItem(Screen.BLACKLIST.name)
    data object Others : NavigationItem(Screen.OTHERS.name)
    data object QRScanner : NavigationItem(Screen.QRSCANNER.name)
    data object Contact : NavigationItem(Screen.CONTACT.name)


}


@Serializable
data class Others(val otherId: Long)

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
            APP_CURRENT_PAGE = NavigationItem.Login.route
            LoginPage(navController, navigation.navigateToHome)
        }

        composable(NavigationItem.Home.route) {
            APP_CURRENT_PAGE = NavigationItem.Home.route
            Home(navController = navController, viewModelProvider)
        }

        composable(NavigationItem.BlogDetail.route) {
            APP_CURRENT_PAGE = NavigationItem.BlogDetail.route
            BlogDetailPage(navController, viewModelProvider)
        }

        composable(NavigationItem.Profile.route) {
            APP_CURRENT_PAGE = NavigationItem.Profile.route
            ProfilePage(navController, viewModelProvider)
        }

        //        composable(NavigationItem.Profile.route, arguments = listOf(navArgument("id") {
        //            type = NavType.IntType
        //        })) { backStackEntry ->
        //            val id = backStackEntry.arguments?.getInt("id") ?: 1 //            ProfileOtherPage(id)
        //        }

        composable(NavigationItem.Discovery.route) {
            APP_CURRENT_PAGE = NavigationItem.Discovery.route
            DiscoveryPage(navController, viewModelProvider)
        }

        //        composable(NavigationItem.Video.route) {
        //            VideoPage()
        //        }
        composable(NavigationItem.ReelPageDemo.route) {
            APP_CURRENT_PAGE = NavigationItem.ReelPageDemo.route
            ReelPageDemo(navController, viewModelProvider)
        }

        //        composable(NavigationItem.Video.route) {
        //            VideoPage()
        //        }

        composable(NavigationItem.Publish.route) {
            APP_CURRENT_PAGE = NavigationItem.Publish.route
            PublishPage(navController, viewModelProvider)
        }

        composable(NavigationItem.PublishDetail.route) {
            APP_CURRENT_PAGE = NavigationItem.PublishDetail.route
            PublishDetailPage(navController, viewModelProvider)
        }

        composable(NavigationItem.ChatDetails.route) {
            APP_CURRENT_PAGE = NavigationItem.ChatDetails.route
            ChatDetailsPage(navController, viewModelProvider)
        }

        composable(NavigationItem.GDMap.route) {
            APP_CURRENT_PAGE = NavigationItem.GDMap.route
            GDMapPage()
        }

        composable(NavigationItem.PictureDisplay.route) {
            APP_CURRENT_PAGE = NavigationItem.PictureDisplay.route
            PictureDisplay(navController, viewModelProvider)
        }

        composable(NavigationItem.VideoDisplay.route) {
            APP_CURRENT_PAGE = NavigationItem.VideoDisplay.route
            VideoDisplay(navController, viewModelProvider)
        }

        composable(NavigationItem.Search.route) {
            APP_CURRENT_PAGE = NavigationItem.Search.route
            SearchPage(navController, viewModelProvider)
        }

        composable(NavigationItem.SearchResult.route) {
            APP_CURRENT_PAGE = NavigationItem.SearchResult.route
            SearchResultPage(navController, viewModelProvider)
        }

        composable(NavigationItem.DiscoveryDetail.route) {
            APP_CURRENT_PAGE = NavigationItem.DiscoveryDetail.route
            DiscoveryDetailPage(navController, viewModelProvider)
        }

        composable(NavigationItem.ImagePreview.route) {
            APP_CURRENT_PAGE = NavigationItem.ImagePreview.route
            ImagePreviewPage(navController = navController, viewModelProvider)
        }

        composable(NavigationItem.ReportUser.route) {
            APP_CURRENT_PAGE = NavigationItem.ReportUser.route
            ReportUserPage(navHostController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.CollectionDetail.route) {
            APP_CURRENT_PAGE = NavigationItem.CollectionDetail.route
            CollectionDetail(navHostController = navController,
                viewModelProvider = viewModelProvider)
        }

        //        composable(NavigationItem.Others.route) {
        //            OthersPage(navController = navController, viewModelProvider = viewModelProvider)
        //        }
        composable<Others> { backEntry ->
            val others: Others = backEntry.toRoute()
            APP_CURRENT_PAGE = "Others"
            OthersPage(navController = navController,
                viewModelProvider = viewModelProvider,
                otherId = others.otherId)
        }

        composable(NavigationItem.Fans.route) {
            FansPage(navHostController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.Follows.route) {
            FollowsPage(navHostController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.ProfileEdit.route) {
            ProfileEditPage(navHostController = navController,
                viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.Setting.route) {
            SettingPage(navController = navController, viewModelProvider = viewModelProvider)
        }

        composable(NavigationItem.ChatSettings.route) {
            ChatSettingsPage(navController, viewModelProvider)
        }

        composable(NavigationItem.Cache.route) {
            ClearCachPage(navController)
        }


        composable(NavigationItem.AccountSecure.route) {
            AccountSecurePage(navController)
        }

        composable(NavigationItem.Notification.route) {
            NotificationPage(navController, viewModelProvider)
        }
        composable(NavigationItem.Signout.route) {
            SignoutPage(navController, viewModelProvider)
        }

        composable(NavigationItem.CancelAgreemet.route) {
            CancelAgreementPage(navController, viewModelProvider)
        }
        composable(NavigationItem.FeedBack.route) {
            FeedbackPage(navController)
        }

        composable(NavigationItem.ChatHistory.route) {
            ChatHistoryPage(navHostController = navController,
                viewModelProvider = viewModelProvider)
        }
        composable(NavigationItem.BrowHistory.route) {
            BrowserHistoryPage(navController, viewModelProvider)
        }
        composable(NavigationItem.PrivacySettings.route) {
            PrivacySettingsPage(navController)
        }

        composable(NavigationItem.BlackList.route) {
            BlackListPage(navController)
        }

        composable(NavigationItem.QRScanner.route) {
            QRScannerPage(navController)
        }
        composable(NavigationItem.Contact.route) {
            ContactPage(navController, viewModelProvider)
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

    val navigateToSettingPage: () -> Unit = {
        navController.navigate(NavigationItem.Setting.route)
    }

    val navigateToCachePage: () -> Unit = {
        navController.navigate(NavigationItem.Cache.route)
    }

    val navigateToAccountSecurePage: () -> Unit = {
        navController.navigate(NavigationItem.AccountSecure.route)
    }

    val navigateToSignoutPage: () -> Unit = {
        navController.navigate(NavigationItem.Signout.route)
    }

    val navigateToCancelAgreementPage: () -> Unit = {
        navController.navigate(NavigationItem.CancelAgreemet.route)
    }

    val navigateToFeedBackPage: () -> Unit = {
        navController.navigate(NavigationItem.FeedBack.route)
    }

    val navigationToBrowserHistory: () -> Unit = {
        navController.navigate(NavigationItem.BrowHistory.route)
    }


    val navigationToPrivacySettings: () -> Unit = {
        navController.navigate(NavigationItem.PrivacySettings.route)
    }


    val navigationToBlackList: () -> Unit = {
        navController.navigate(NavigationItem.BlackList.route)
    }

    val navigationToQRScanner: () -> Unit = {
        navController.navigate(NavigationItem.QRScanner.route)
    }
    val navigationToContact: () -> Unit = {
        navController.navigate(NavigationItem.Contact.route)
    }


}