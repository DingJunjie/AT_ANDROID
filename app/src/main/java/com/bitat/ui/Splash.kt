package com.bitat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import com.bitat.repository.store.TokenStore
import com.bitat.router.AtNavigation
import com.bitat.router.NavigationItem

@Composable
fun Splash(navHostController: NavHostController) {
    LaunchedEffect(Unit) {
        val token = TokenStore.fetchToken()
        if (token.isNullOrEmpty()) {
            navHostController.navigate(NavigationItem.Login.route)
        } else {
            navHostController.navigate(NavigationItem.Home.route)
        }
    }
}