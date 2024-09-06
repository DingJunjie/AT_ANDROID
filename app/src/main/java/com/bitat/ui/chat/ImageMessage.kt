package com.bitat.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import com.bitat.utils.QiNiuUtil.QINIU_CHAT_PREFIX
import com.bitat.utils.ScreenUtils

@Composable
fun SenderImage(url: String) {
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth(0.6f)
                .heightIn(min = 120.dp, max = ScreenUtils.screenHeight.times(0.35).dp)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            AsyncImage(
                model = QINIU_CHAT_PREFIX + url,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun RecipientImage(url: String) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth(0.6f)
                .heightIn(min = 120.dp, max = ScreenUtils.screenHeight.times(0.35).dp)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            AsyncImage(
                model = QINIU_CHAT_PREFIX + url,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }
}