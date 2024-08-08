package com.bitat.ui.video

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitat.ui.reel.CuExoPlayer
import com.bitat.viewModel.ReelViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelPage(vm: ReelViewModel = viewModel()) {
    val pagerState = rememberPagerState(pageCount = { vm.reelList.size })
    val vpState =
        rememberVideoPlayerState(videoSource = Uri.parse("https://www.w3schools.com/html/movie.mp4"))
    val vpState2 =
        rememberVideoPlayerState(videoSource = Uri.parse("https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4"))

    val source = arrayOf("https://www.w3schools.com/html/movie.mp4",
        "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4")


    Box(modifier = Modifier.fillMaxWidth().height(200.dp) //            .background(Color.White)
    ) {
        VerticalPager(state = pagerState,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()) { page ->
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()
                .background(if (page % 2 == 0) Color.Transparent else Color.Yellow)) { //                VideoPlayer(state = if (page == 0) vpState else vpState2)

                CuExoPlayer(data = source[page], modifier = Modifier.fillMaxSize(), true)
            }
        }

    }
}