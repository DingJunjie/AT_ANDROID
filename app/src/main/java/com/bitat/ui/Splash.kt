package com.bitat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import com.bitat.repository.dto.req.GetUserInfoDto
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.router.NavigationItem

@Composable
fun Splash(navHostController: NavHostController) {
    LaunchedEffect(Unit) {
        val token = TokenStore.fetchToken()
        if (token.isNullOrEmpty()) {
            navHostController.navigate(NavigationItem.Login.route)
        } else {
            val user = TokenStore.getUser()
            if (user == null) {
                navHostController.navigate(NavigationItem.Login.route)
            } else {
                UserStore.initUserInfo(user)
                UserReq.userInfo(UserInfoDto(userId = user.id)).await().map {
                    UserStore.updateByUserInfo(it)
                    navHostController.navigate(NavigationItem.Home.route)
                }.errMap {
                    println("get error message ${it.msg}")
                }
            }
        }
    }
}