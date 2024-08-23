package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.CommonTopBar
import com.bitat.viewModel.ProfileViewModel

@Composable
fun FollowsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        CommonTopBar(title = "关注列表", backFn = { navHostController.popBackStack() })
    }, modifier = Modifier.padding(top = statusBarHeight)) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            state.followsList.forEach {
                Text(it.nickname)
            }
        }
    }
}

