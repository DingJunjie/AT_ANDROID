package com.bitat.ui.component

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.reel.CuExoPlayer
import com.bitat.ui.video.rememberVideoPlayerState
import com.bitat.utils.ScreenUtils

@Composable
fun BlogVideo(dto: BlogBaseDto, height: Int) {
    val videoState = rememberVideoPlayerState(videoSource = Uri.parse(dto.resource.video))
    Surface(shape = RoundedCornerShape(20.cdp),
        modifier = Modifier.fillMaxWidth().padding(start = ScreenUtils.screenWidth.times(0.11).dp)
            .height(height.dp)) { //视频
        //        VideoPlayer(
        //            state = videoState, modifier = Modifier
        //                .fillMaxWidth()
        ////                .height((maxHeight + 200).dp)
        //                .background(Color.Black)
        //        )

        CuExoPlayer(data = dto.resource.video, modifier = Modifier.fillMaxWidth(), true)

        CuLog.info(CuTag.Blog, "BlogVideo---------") //        ReelPage()
    }
}