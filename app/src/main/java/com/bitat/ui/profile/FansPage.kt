package com.bitat.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ext.cdp
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.store.UserStore
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.hintTextColor
import com.bitat.ui.theme.toolBarIcon70
import com.bitat.viewModel.ProfileViewModel

@Composable
fun FansPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()
    Scaffold(topBar = {
        CommonTopBar(title = state.user.nickname,
            backFn = { navHostController.popBackStack() },
            endButton = {
                SvgIcon(modifier = Modifier.size(35.dp).padding(end = 30.cdp).clickable {

                }, contentDescription = "", path = "svg/search.svg", tint = toolBarIcon70)
            })
    }, modifier = Modifier.padding(top = statusBarHeight)) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(state.fansList) { index, item ->
                fansItem(item)
                if (index != state.fansList.size - 1) Spacer(modifier = Modifier.height(30.cdp))
            }
        }
    }
}

@Composable
fun fansItem(item: UserBase1Dto) {
    Row(modifier = Modifier.padding(start = 30.cdp, end = 30.cdp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.width(10.dp))
        Avatar(item.profile, size = 60)
        Spacer(modifier = Modifier.width(20.dp))
        Column() {
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
    }
}


