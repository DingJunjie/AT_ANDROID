package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.AtNavigation
import com.bitat.ui.reel.CuExoPlayer
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.ReelViewModel

@Composable
fun BlogVideo(modifier: Modifier=Modifier,
    dto: BlogBaseDto,
    height: Int,
    isPlaying: Boolean = false,
    coverIsFull: Boolean = true,
    needRoundedCorner: Boolean = true,
    navController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[BlogViewModel::class]
    val detailsVm = viewModelProvider[ReelViewModel::class]

    Surface(shape = RoundedCornerShape(if (needRoundedCorner) 20.cdp else 0.cdp),
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clickable {
                vm.setCurrentBlog(dto)
                detailsVm.setCurrentBlog(dto)
                AtNavigation(navController).navigateToVideo()
            }) {
        AsyncImage(
            model = dto.cover,
            modifier = Modifier
                .clip(RoundedCornerShape(if (needRoundedCorner) 8.dp else 0.dp))
                .fillMaxWidth()
                .height(height.dp)
                .background(Color.Transparent),
            contentDescription = null,
            contentScale = if (coverIsFull) ContentScale.Crop else ContentScale.FillWidth
        )

        if (isPlaying) {
            CuExoPlayer(
                data = dto.resource.video,
                modifier = Modifier.fillMaxWidth(),
                cover = dto.cover,
                isFixHeight = true,
                soundShow = true
            )
        }
        CuLog.info(CuTag.Blog, "BlogVideo--------- ${dto.id}")
    }
}