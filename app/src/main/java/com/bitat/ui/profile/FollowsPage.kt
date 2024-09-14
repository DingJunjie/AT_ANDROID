package com.bitat.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.router.Others
import com.bitat.ui.common.ListFootView
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.toolBarIcon70
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.FollowBtnViewModel
import com.bitat.viewModel.FollowsViewModel
import com.bitat.viewModel.OthersViewModel
import com.bitat.viewModel.ProfileViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

@Composable
fun FollowsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: FollowsViewModel = viewModel()
    val state by vm.state.collectAsState()
    val othersVm: OthersViewModel = viewModelProvider[OthersViewModel::class]
    val followVm = viewModelProvider[FollowBtnViewModel::class]
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        vm.getMyFollows()
    }

    fun loadMore() {
        state.followsList.last().let {
            vm.getMyFollows(it.followTime)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect { layoutInfo ->
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
            if (lastVisibleItemIndex == state.followsList.size - 1) {
                vm.isLoadMore(true)
                loadMore()
            }
        }
    }



    Scaffold(topBar = {
        CommonTopBar(title = UserStore.userInfo.nickname,
            backFn = { navHostController.popBackStack() },
            endButton = {
                SvgIcon(modifier = Modifier.size(35.dp).padding(end = 30.cdp).clickable {

                }, contentDescription = "", path = "svg/search.svg", tint = toolBarIcon70)
            },
            paddingStatus = true,
            isBg = true)
    }, modifier = Modifier) { padding ->
        LazyColumn(state = listState, modifier = Modifier.padding(padding)) {
            itemsIndexed(state.followsList) { index, item ->
                FansItem(item, state.flag, itemTap = {
//                    othersVm.initUserId(item.id)
//                    navHostController.navigate(NavigationItem.Others.route)
                    navHostController.navigate(Others(otherId = item.id))
                }, followFn = {

                    followVm.followUser(item.rel, item.revRel, item.id, onSuccess = {
                        when (it) {
                            DEFAULT -> vm.removeFollow(index)
                            FOLLOWED -> vm.updateCurrentFollow(index, item.copy(rel = it))

                        }

                    }, onError = { error ->
                        when (error.code) {
                            -1 -> ToastModel(error.msg, ToastModel.Type.Error, 1000).showToast()
                        }

                    })
                }, followText = RelationUtils.toRelationContent(item.rel, item.revRel))
                if (index != state.followsList.size - 1) Spacer(modifier = Modifier.height(30.cdp))
            }
            item {
                ListFootView(state.isLoadMore, state.loadResp) {
                    loadMore()
                }
            }
        }
    }
}

