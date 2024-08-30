package com.bitat.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitat.R
import com.bitat.ui.common.rememberAsyncPainter

@Composable
fun Avatar(url: String, modifier: Modifier = Modifier, size: Int = 40, tapFn: () -> Unit = {}) {
    Surface(
        shape = CircleShape, modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .size(size.dp)
                .clickable {
                    tapFn()
                }
                .border(width = size.dp, color = Color.Transparent, shape = CircleShape)
                .paint(painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .placeholder(R.mipmap.ic_launcher)  // 加载中的占位图
                        .error(R.mipmap.ic_launcher)  // 加载失败时的默认图片
                        .build()
                ), contentScale = ContentScale.Crop)
        ) {
        }
    }
}


