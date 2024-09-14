package com.bitat.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ext.toAmountUnit
import com.bitat.repository.consts.BLACKLIST
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.router.AtNavigation
import com.bitat.router.Others
import com.bitat.state.ReelType
import com.bitat.state.SearchType
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.MediaGrid
import com.bitat.ui.profile.OthersPage
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.FollowBtnViewModel
import com.bitat.viewModel.ReelViewModel
import com.bitat.viewModel.SearchViewModel


@Composable
fun SearchResultPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[SearchViewModel::class]
    val state by vm.searchState.collectAsState()

    val blogVm = viewModelProvider[BlogViewModel::class]
    val detailsVm = viewModelProvider[ReelViewModel::class]
    val followVm = viewModelProvider[FollowBtnViewModel::class]

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }

    Scaffold(modifier = Modifier.padding(top = statusBarHeight), topBar = {
        ResultTopBar(navHostController, state.keyword, {
            vm.updateKeyword(it)
        }, selectedTabIndex, tapFn = {
            selectedTabIndex = it
            when (selectedTabIndex) {
                1 -> {
                    vm.searchVideo(state.keyword)
                }

                2 -> {
                    vm.searchUser(state.keyword)
                }

                else -> {}
            }
        })
    }) { padding ->
        Surface(
            modifier = Modifier.padding(padding)
        ) {
            when (selectedTabIndex) {
                0 -> Text("0")
                1 -> MediaGrid(mediaList = state.searchVideoResult) { item ->
                    val index = state.searchVideoResult.indexOf(item)
                    if (index >= 0) {
                        detailsVm.setPageType(ReelType.SEARCH)
                        detailsVm.setKevWords(state.keyword)
                        detailsVm.setIndex(index)
                        detailsVm.setResList(state.searchVideoResult.toList())
                        AtNavigation(navHostController).navigateToVideo()
                    }
                }

                2 -> UserResult(
                    state.searchUserResult,
                    followTap = { user ->
                        followVm.followUser(
                            user.rel,
                            user.revRel,
                            user.id,
                            onSuccess = { rel ->
                                user.rel = rel
                                vm.updateUser(user)
                            },
                            onError = { })
                    }, flag = state.flag, itemTap = {
                        navHostController.navigate(Others(otherId = it.id))
                    })
            }
        }
    }
}

@Composable
fun UserResult(
    userList: List<UserBase1Dto>,
    followTap: (UserBase1Dto) -> Unit,
    itemTap: (UserBase1Dto) -> Unit, flag: Int
) {
    LazyColumn {
        items(userList) { user ->
            Surface(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) {
                UserItem(user, flag, followTap, itemTap)
            }
        }
    }
    if (flag < 0) Text(text = "")
}

@Composable
fun UserItem(
    user: UserBase1Dto, flag: Int,
    followTap: (UserBase1Dto) -> Unit,
    itemTap: (UserBase1Dto) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { itemTap(user) }
    ) {
        Avatar(url = user.profile)
        Column(modifier = Modifier.weight(1f)) {
            Text(user.nickname, fontSize = 12.sp)
            Text("粉丝：" + user.fans.toAmountUnit(), fontSize = 12.sp, color = Color.Gray)
            Text("艾特号：" + user.account, fontSize = 12.sp, color = Color.Gray)
        }
        Surface(shape = CircleShape) {
            TextButton(
                onClick = { followTap(user) },
                modifier = Modifier
                    .background(Color.LightGray)
                    .height(40.dp)
                    .width(70.dp),
                shape = CircleShape,
                enabled = user.rel != BLACKLIST, // 已拉黑不能点击
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 4.dp)
            ) {
                Text(RelationUtils.toFollowContent(user.rel))
            }
        }
        if (flag < 0) Text(text = "")
    }
}

@Composable
fun ResultTopBar(
    navHostController: NavHostController,
    keyword: String,
    updateKeyword: (String) -> Unit,
    selectedTabIndex: Int,
    tapFn: (Int) -> Unit
) {
    Column(modifier = Modifier.height(100.dp)) {
        SearchTopBar(navHostController = navHostController, keyword = keyword, updateKeyword = {
            updateKeyword(it)
        }, searchTapFn = {})
        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            SearchType.entries.forEachIndexed { index, type ->
                val title = SearchType.getUiContent(type)
                Tab(text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { tapFn(index) })
            }
        }
    }
}


