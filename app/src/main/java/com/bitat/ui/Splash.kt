package com.bitat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.store.RoomStore
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.ui.publish.TextToImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun Splash(navHostController: NavHostController) {
    LaunchedEffect(Unit) {
        val user = TokenStore.getUser()
        if (user == null) {
            navHostController.navigate(NavigationItem.Login.route)
        } else {
            UserStore.initUserInfo(user)
            val token = TokenStore.fetchToken()
            if (token.isNullOrEmpty()) {
                navHostController.navigate(NavigationItem.Login.route)
            } else {
                UserReq.userInfo(UserInfoDto(userId = user.id)).await().map {
                    UserStore.updateByUserInfo(it)
                    navHostController.navigate(NavigationItem.Home.route)
                }.errMap {
                    println("get error message ${it.msg}")
                    navHostController.navigate(NavigationItem.Login.route)
                }
            }
        }
    }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) { //            TextToImage()

        }
    }
}