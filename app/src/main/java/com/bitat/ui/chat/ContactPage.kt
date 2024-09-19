package com.bitat.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.cdp
import com.bitat.repository.dto.resp.toUserHomeDto
import com.bitat.router.AtNavigation
import com.bitat.router.NavigationItem
import com.bitat.router.Others
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.profile.FansItem
import com.bitat.viewModel.ChatViewModel
import com.bitat.viewModel.ContactViewModel

/**
 *    author : shilu
 *    date   : 2024/9/19  17:01
 *    desc   : 通讯录
 */
@Composable
fun ContactPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {

    val vm: ContactViewModel = viewModel()
    val state = vm.state.collectAsState()
    val chatVm = viewModelProvider[ChatViewModel::class]

    LaunchedEffect(Unit){
        vm.getContact(true)
    }
    Scaffold(topBar = {
        CommonTopBar(modifier = Modifier,
            title = "朋友",
            backFn = { AtNavigation(navHostController).navigateToHome() },
            isBg = true,
            paddingStatus = true)
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.value.contactList) { index, item ->
                    FansItem(item,
                        state.value.flag,
                        itemTap = { //                    othersVm.initUserId(item.id)
                            navHostController.navigate(Others(otherId = item.id))
                        },
                        followFn = {
                            chatVm.createRoomByProfile(item.toUserHomeDto())
                            navHostController.navigate(NavigationItem.ChatDetails.route)
                        },
                        followText = "发私信")
                    if (index != state.value.contactList.size - 1) Spacer(modifier = Modifier.height(
                        30.cdp))
                }
            }
        }
    }
}