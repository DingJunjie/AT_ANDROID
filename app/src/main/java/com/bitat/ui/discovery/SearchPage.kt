package com.bitat.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.ext.cdp
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.po.SearchHistoryPo
import com.bitat.router.NavigationItem
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.theme.Typography
import com.bitat.viewModel.SearchViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun SearchPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[SearchViewModel::class]
    val state by vm.searchState.collectAsState()

    LaunchedEffect(Dispatchers.Main) {
        vm.atRankingList()
        vm.getHistory()
    }

    Scaffold(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = statusBarHeight),
        topBar = {
            SearchTopBar(navHostController, keyword = state.keyword, updateKeyword = {
                vm.updateKeyword(it)
            }, searchTapFn = {
                vm.insertHistory()
                navHostController.navigate(NavigationItem.SearchResult.route)
            })
        }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchHistory(state.historyList, deleteItem = {
                vm.deleteKeyword(it)
            }) {
                vm.updateKeyword(it.content)
                navHostController.navigate(NavigationItem.SearchResult.route)
            }
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "艾特榜",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 30.cdp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 设置每行的列数
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(30.cdp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.rankingList.size) { index -> //
                    GridItem(state.rankingList[index]) {
                        // 点击事件
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistory(
    historyList: List<SearchHistoryPo>,
    deleteItem: (String) -> Unit,
    chooseItem: (SearchHistoryPo) -> Unit
) {
    val isEditing = remember {
        mutableStateOf(false)
    }


    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("历史记录", fontSize = 14.sp)
            Text("编辑", fontSize = 14.sp, modifier = Modifier.clickable {
                isEditing.value = !isEditing.value
            })
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            historyList.map { that ->
                HistoryChip(content = that.content, isEditing = isEditing.value, deleteItem) {
                    chooseItem(that)
                }
            }
        }
    }
}

@Composable
fun HistoryChip(
    content: String, isEditing: Boolean = false, deleteFn: (String) -> Unit, tapFn: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 0.dp, horizontal = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
                vertical = 5.dp, horizontal = 10.dp
            )
        ) {

            Text(content, modifier = Modifier.clickable {
                tapFn()
            }, style = Typography.bodySmall.copy(fontSize = 14.sp))

            if (isEditing) Icon(Icons.Filled.Close, contentDescription = "", modifier = Modifier
                .clickable {
                    deleteFn(content)
                }
                .size(20.dp))
        }
    }
}

@Composable
fun GridItem(dto: BlogPartDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, //            .aspectRatio(1f)
        shape = RoundedCornerShape(8.dp), //        elevation = CardElevation(4.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = dto.cover,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.Transparent),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

