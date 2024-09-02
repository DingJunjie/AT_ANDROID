package com.bitat.ui.chat

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.bitat.ui.video.VideoPlayer
import com.bitat.utils.QiNiuUtil.QINIU_PUB_PREFIX
import com.bitat.utils.ScreenUtils

@Composable
fun SenderVideo(cover: String, video: String) {
    val isPlaying = remember {
        mutableStateOf(false)
    }
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth(0.6f)
                .heightIn(min = 200.dp, max = ScreenUtils.screenHeight.times(0.5).dp)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            if (isPlaying.value)
                VideoPlayer(uri = (QINIU_PUB_PREFIX + video).toUri())
            else AsyncImage(
                model = QINIU_PUB_PREFIX + cover,
                contentDescription = "",
                modifier = Modifier.clickable { isPlaying.value = true },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun RecipientVideo(cover: String, video: String) {
    val isPlaying = remember {
        mutableStateOf(false)
    }
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth(0.6f)
                .heightIn(min = 200.dp, max = ScreenUtils.screenHeight.times(0.5).dp)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            if (isPlaying.value)
                VideoPlayer(uri = (QINIU_PUB_PREFIX + video).toUri())
            else AsyncImage(
                model = QINIU_PUB_PREFIX + cover,
                contentDescription = "",
                modifier = Modifier.clickable { isPlaying.value = true },
                contentScale = ContentScale.Crop
            )
        }
    }
}

