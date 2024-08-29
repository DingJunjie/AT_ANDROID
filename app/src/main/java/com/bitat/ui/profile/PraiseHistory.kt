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
import com.bitat.ui.common.ListFootView
import com.bitat.ui.component.MediaGrid
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ProfileViewModel

/**
 *    author : shilu
 *    date   : 2024/8/26  11:50
 *    desc   : 赞过
 */
@Composable
fun PraiseHistory(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {

    val vm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.getMyPraise()
    }

    LaunchedEffect(state.isAtBottom) {
        CuLog.debug(CuTag.Blog, "PraiseHistory layoutInfo，滑动到底部")
        if (state.profileType == 3) {
            if (state.myPraise.isNotEmpty()) {
                val lastTime = state.myPraise.last().createTime
                vm.getMyPraise(lastTime = lastTime)
            }
        }

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min =
                ScreenUtils.screenHeight.dp
            )
    ) {
        if (state.isAtBottom) {
            Text(text = "111111111111111111111")
        }
        MediaGrid(mediaList = state.myPraise)
        ListFootView(state.isFootShow, state.httpState) {
            if (state.myPraise.isNotEmpty()) {
                val lastTime = state.myPraise.last().createTime
                vm.getMyPraise(lastTime = lastTime)
            }
        }

    }

}