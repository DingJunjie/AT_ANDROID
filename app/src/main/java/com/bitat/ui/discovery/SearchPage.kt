package com.bitat.ui.discovery

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.MainCo
import com.bitat.repository.sqlDB.SearchHistoryDB
import com.bitat.repository.sqlDB.SqlDB
import com.bitat.repository.store.UserStore
import com.bitat.router.NavigationItem
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.BackButton
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.PublishViewModel
import com.bitat.viewModel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[SearchViewModel::class]
    val state by vm.searchState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = statusBarHeight),
        topBar = {
            SearchTopBar(
                navHostController,
                keyword = state.keyword,
                updateKeyword = {
                    vm.updateKeyword(it)
                }, searchTapFn = {
                    navHostController.navigate(NavigationItem.SearchResult.route)
                })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchHistory()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistory() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("历史记录", fontSize = 14.sp)
            Text("编辑", fontSize = 14.sp)
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            HistoryChip(content = "hello world")
            HistoryChip(content = "hello world")
            HistoryChip(content = "123")
            HistoryChip(content = "abcdefghijklmnopqrstuvwxyz")
            HistoryChip(content = "hello world")
        }
    }
}

@Composable
fun HistoryChip(content: String, tapFn: (String) -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(vertical = 0.dp, horizontal = 6.dp)
    ) {
        TextButton(
            onClick = {
                tapFn(content)
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color.LightGray
            ),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
        ) {
            Text(content)
        }
    }
}

