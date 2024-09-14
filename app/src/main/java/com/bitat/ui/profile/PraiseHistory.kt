package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.state.ReelType
import com.bitat.ui.common.ListFootView
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.MediaGrid
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ProfileViewModel
import com.bitat.viewModel.ReelViewModel

/**
 *    author : shilu
 *    date   : 2024/8/26  11:50
 *    desc   : 赞过
 */
@Composable
fun PraiseHistory(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {

    val vm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()
    val detailsVm = viewModelProvider[ReelViewModel::class]

    LaunchedEffect(Unit) {
        vm.getMyPraise()
    }
    fun loadMore() {
        if (state.profileType == 3) {
            if (state.myPraise.isNotEmpty()) {
                val lastTime = state.myPraise.last().createTime
                vm.getMyPraise(lastTime = lastTime)
            } else {
                vm.getMyPraise()
            }
        }
    }

    LaunchedEffect( state.isAtBottom) {
        loadMore()

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min =
                ScreenUtils.screenHeight.dp-56.dp- statusBarHeight
            )
    ) {
        MediaGrid(mediaList = state.myPraise){ item ->
            val index = state.myPraise.indexOf(item)
            if (index >= 0) {
                detailsVm.setPageType(ReelType.LIKE)
                detailsVm.setIndex(index)
                detailsVm.setResList(state.myPraise.toList())
                AtNavigation(navHostController).navigateToVideo()
            }
        }
        ListFootView(state.isFootShow, state.httpState) {
            loadMore()
        }
    }

}