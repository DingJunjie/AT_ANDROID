package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitat.R
import com.bitat.ui.common.SvgIcon

@Composable
fun Avatar(url: String, modifier: Modifier = Modifier, size: Int = 40, showFollow: Boolean = false, tapFn: () -> Unit = {}, follow: () -> Unit = {}) {

    Box(modifier = Modifier.width((size + 5).dp)) {
        Box(modifier = modifier.size(size.dp).clip(CircleShape).clickable {
                tapFn()
            }.border(width = size.dp, color = Color.Transparent, shape = CircleShape)
            .paint(painter = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                .data(url).placeholder(R.mipmap.ic_launcher)  // 加载中的占位图
                .error(R.mipmap.ic_launcher)  // 加载失败时的默认图片
                .build()), contentScale = ContentScale.Crop)) {

        }

        if (showFollow)
            Box(modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).offset(x = 2.dp).clip(CircleShape).background(Color.Black)
            .clickable(onClick = { follow( ) })) {
            SvgIcon(path = "svg/add.svg", contentDescription = "关注", tint = Color.White)
        }

    }

}


