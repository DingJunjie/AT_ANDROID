package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.cdp
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.hintTextColor
import com.bitat.ui.theme.toolBarIcon70
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.FansViewModel
import com.bitat.viewModel.FollowBtnViewModel
import com.bitat.viewModel.FollowsViewModel
import com.bitat.viewModel.OthersViewModel
import com.bitat.viewModel.ProfileViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

@Composable
fun FansPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: FansViewModel = viewModel()
    val state by vm.state.collectAsState()
    val othersVm: OthersViewModel = viewModelProvider[OthersViewModel::class]

    val followVm = viewModelProvider[FollowBtnViewModel::class]

    LaunchedEffect(Unit) {
        vm.getMyFans()
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
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(state.fansList) { index, item ->
                FansItem(item, state.flag, itemTap = {
                    othersVm.initUserId(item.id)
                    navHostController.navigate(NavigationItem.Others.route)
                }, followFn = {
                    followVm.followUser(item.rel, item.revRel, item.id, onSuccess = {
                        vm.updateCurrentFans(index, item.copy(rel = it))
                    }, onError = { error ->
                        when (error.code) {
                            -1 -> ToastModel(error.msg, ToastModel.Type.Error, 1000).showToast()
                        }

                    })
                }, followText = RelationUtils.toRelationContent(item.rel, item.revRel))
                if (index != state.fansList.size - 1) Spacer(modifier = Modifier.height(30.cdp))
            }
        }
    }
}

@Composable
fun FansItem(item: UserBase1Dto, flag: Int, itemTap: () -> Unit, followFn: () -> Unit, followText: String) {
    Row(modifier = Modifier.padding(start = 30.cdp, end = 30.cdp, top = 10.dp, bottom = 10.dp)
        .clickable { itemTap() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.width(10.dp))
        Avatar(item.profile, size = 55)
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.fillMaxSize().weight(1f)) {
            Text(item.nickname, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text("粉丝：${item.fans}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = hintTextColor))
                Spacer(modifier = Modifier.width(30.cdp))
                Text("关注：${item.follows}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = hintTextColor))
            }
        }
        Column(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = {
                followFn()
            }, indication = null, interactionSource = remember { MutableInteractionSource() }),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(modifier = Modifier.padding(top = 10.dp,
                bottom = 10.dp,
                start = 20.dp,
                end = 20.dp),
                text = followText,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
        }
        if (flag < 0) Text("")
    }
}


