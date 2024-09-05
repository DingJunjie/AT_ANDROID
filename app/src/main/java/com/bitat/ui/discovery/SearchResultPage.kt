package com.bitat.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.state.SearchType
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.MediaGrid
import com.bitat.ui.profile.AvatarWithShadow
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.SearchViewModel


@Composable
fun SearchResultPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[SearchViewModel::class]
    val state by vm.searchState.collectAsState()

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

                4 -> {
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
                1 -> MediaGrid(mediaList = state.searchVideoResult)
                2 -> Text("2")
                3 -> Text("3")
                4 -> UserResult(state.searchUserResult)
            }
        }
    }
}

@Composable
fun UserResult(userList: List<UserBase1Dto>) {
    LazyColumn {
        items(userList) { user ->
            Surface(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) {
                UserItem(user)
            }
        }
    }
}

@Composable
fun UserItem(user: UserBase1Dto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Avatar(url = user.profile)
        Column(modifier = Modifier.weight(1f)) {
            Text(user.nickname, fontSize = 12.sp)
            Text("粉丝：" + user.fans.toAmountUnit(), fontSize = 12.sp, color = Color.Gray)
            Text("艾特号：" + user.account, fontSize = 12.sp, color = Color.Gray)
        }
        Surface(shape = CircleShape) {
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .background(Color.LightGray)
                    .height(40.dp)
                    .width(70.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 4.dp)
            ) {
                Text(RelationUtils.toRelationContent(user.rel, user.revRel))
            }
        }
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


