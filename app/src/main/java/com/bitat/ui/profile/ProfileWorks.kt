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
import com.bitat.router.AtNavigation
import com.bitat.state.ReelType
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.MediaGrid
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ProfileViewModel
import com.bitat.viewModel.ReelViewModel

@Composable
fun ProfileWorks(userId: Long, navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()
    val detailsVm = viewModelProvider[ReelViewModel::class]

    LaunchedEffect(Unit) {
        vm.getMyWorks(userId)
    }

    LaunchedEffect(state.isAtBottom) {
        if (state.profileType == 2) {
            if (state.myWorks.isNotEmpty()) {
                val lastTime = state.myWorks.last().createTime
                vm.getMyWorks(userId = UserStore.userInfo.id, lastTime = lastTime)
            }else{
                vm.getMyWorks(userId = UserStore.userInfo.id)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().heightIn(min = ScreenUtils.screenHeight.dp-56.dp- statusBarHeight)) {
        MediaGrid(mediaList = state.myWorks){ item ->
            val index = state.myWorks.indexOf(item)
            if (index >= 0) {
                detailsVm.setPageType(ReelType.PHOTO)
                detailsVm.setIndex(index)
                detailsVm.setSearchList(state.myWorks.toList())
                AtNavigation(navHostController).navigateToVideo()
            }
        }
    }
}