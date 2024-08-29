package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.repository.store.UserStore
import com.bitat.ui.component.MediaGrid
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ProfileViewModel

@Composable
fun ProfileWorks(userId: Long, navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.getMyWorks(userId)
    }

    LaunchedEffect(state.isAtBottom) {
        if (state.profileType == 2) {
            if (state.myWorks.isNotEmpty()) {
                val lastTime = state.myWorks.last().createTime
                vm.getMyWorks(userId = UserStore.userInfo.id, lastTime = lastTime)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().heightIn(min = ScreenUtils.screenHeight.dp)) {
        MediaGrid(mediaList = state.myWorks)
    }
}