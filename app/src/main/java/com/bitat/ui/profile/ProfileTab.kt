package com.bitat.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ext.clickableWithoutRipple
import com.bitat.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileTabView(
    options: List<String>,
    pagerState: PagerState,
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider,
    content: @Composable PagerScope.(Int) -> Unit
) { //    Column {
    //        ProfileTabBar(pagerState, options)

    val vm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) { index ->

        when (index) {
            0 -> Box {}
            1 -> ProfileWorks(navHostController, viewModelProvider)
            2 -> CollectionTab(navHostController, viewModelProvider = viewModelProvider)
            3 -> PraiseHistory(navHostController, viewModelProvider = viewModelProvider)
        }
    } //    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileTabBar(pagerState: PagerState, options: List<String>, onSelect: (Int) -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()

    ScrollableTabRow(selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .width(10.dp), height = 2.5.dp, color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(), divider = {}) {
        options.forEachIndexed { index, item ->
            val selected = index == pagerState.currentPage
            Text(text = item, color = if (selected) {
                Color.Black
            } else {
                Color.Gray
            }, fontSize = 17.sp, modifier = Modifier
                .clickableWithoutRipple {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                        onSelect(index)
                    }
                }
                .fillMaxWidth()
                .padding(vertical = 16.dp), textAlign = TextAlign.Center
            )
        }
    }
}