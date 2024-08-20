package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.AtNavigation
import com.bitat.ui.reel.CuExoPlayer
import com.bitat.viewModel.BlogViewModel

@Composable
fun BlogVideo(
    dto: BlogBaseDto,
    height: Int,
    isPlaying: Boolean = false,
    navController: NavHostController, viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[BlogViewModel::class]
    val blogState by vm.blogState.collectAsState() //    val videoState = rememberVideoPlayerState(videoSource = Uri.parse(dto.resource.video))
    Surface(
        shape = RoundedCornerShape(20.cdp),
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
            .clickable {
                vm.filterResList()
                vm.setCurrentBlog(dto)
                AtNavigation(navController).navigateToVideo()
            }
    ) { //视频
        //                VideoPlayer(
        //                    state = videoState, modifier = Modifier
        //                        .fillMaxWidth()
        //        //                .height((maxHeight + 200).dp)
        //                        .background(androidx.compose.ui.graphics.Color.Black)
        //                )


//        if (dto.id == currentId) { //item在页面中间才加载视频
        //未选中只加载封面
        AsyncImage(
            model = dto.cover,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(height.dp)
                .background(Color.Transparent),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        if (isPlaying) {
            CuExoPlayer(
                data = dto.resource.video,
                modifier = Modifier.fillMaxWidth(),
                true,
                false
            )
        }

        CuLog.info(CuTag.Blog, "BlogVideo---------") //        ReelPage()
    }
}