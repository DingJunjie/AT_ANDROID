package com.bitat.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ext.cdp
import com.bitat.router.NavigationItem
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.toolBarIcon70
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.OthersViewModel
import com.bitat.viewModel.ProfileViewModel

@Composable
fun FollowsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()
    val othersVm: OthersViewModel = viewModelProvider[OthersViewModel::class]

    Scaffold(topBar = {
        CommonTopBar(title = state.user.nickname,
            backFn = { navHostController.popBackStack() },
            endButton = {
                SvgIcon(modifier = Modifier.size(35.dp).padding(end = 30.cdp).clickable {

                }, contentDescription = "", path = "svg/search.svg", tint = toolBarIcon70)
            })
    }, modifier = Modifier.padding(top = statusBarHeight)) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(state.followsList) { index, item ->
                fansItem(item,itemTap = {
                    othersVm.initUserId(item.id)
                    navHostController.navigate(NavigationItem.Others.route)
                }, followFn = {}, followText = RelationUtils.toRelationContent(item.rel,item.revRel))
                if (index != state.followsList.size - 1) Spacer(modifier = Modifier.height(30.cdp))
            }
        }
    }
}

