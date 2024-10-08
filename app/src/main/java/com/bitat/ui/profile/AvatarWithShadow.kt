package com.bitat.ui.profile

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitat.R
import com.bitat.ui.common.rememberAsyncPainter

@Composable
fun AvatarWithShadow(
    url: String,
    modifier: Modifier = Modifier,
    size: Int = 80,
    needPadding: Boolean = true,
    tapFn: () -> Unit = {}
) {
    Surface(
        shape = CircleShape,
        modifier = Modifier
            .padding(start = if (needPadding) 15.dp else 0.dp, top = 15.dp)
            .clickable {
                tapFn()
            }
            .shadow(elevation = 5.dp, shape = CircleShape)
    ) {
        Box(
            modifier = modifier
                .size(size.dp)
                .border(width = 38.dp, color = Color.Transparent, shape = CircleShape)
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