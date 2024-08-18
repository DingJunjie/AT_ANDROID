package com.bitat.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.ui.common.SvgIcon
import com.bitat.viewModel.ImagePreviewViewModel

/**
 *    author : shilu
 *    date   : 2024/8/16  17:51
 *    desc   : 图片预览页面
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePreviewPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ImagePreviewViewModel::class]
    val state by vm.imagePreviewState.collectAsState()



    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(paddingValues = innerPadding)
        ) {

            ImageBanner(state.imgList.toList())
            IconButton(onClick = { navController.popBackStack() }) {
                SvgIcon(
                    path = "svg/arrow-left.svg",
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
            }

        }

    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageBanner(imgList: List<String>) {
    val pageState: PagerState = rememberPagerState(pageCount = { imgList.size })
    HorizontalPager(state = pageState, modifier = Modifier.fillMaxSize()) { index ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = imgList[index],
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentDescription = null,
                contentScale = ContentScale.FillWidth //宽度撑满
            )
        }
    }
}