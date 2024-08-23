package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.utils.ReportUtils
import com.bitat.utils.ScreenUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaGrid(mediaList: List<BlogPartDto>) {
    val width = (ScreenUtils.screenWidth / 3)
    val height = width * 1.33

    FlowRow(
        verticalArrangement = Arrangement.Top,
    ) {
        mediaList.forEach {
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(height.dp)
                    .padding(1.dp)
            ) {
                AsyncImage(
                    model = it.cover.ifEmpty { it.resource.images.first() },
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )

                Icon(
                    Icons.Filled.AccountBox,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }

//    LazyVerticalGrid(
//        columns = GridCells.Fixed(3), // 设置固定列数为2
//        contentPadding = PaddingValues(16.dp), // 设置内边距
//        verticalArrangement = Arrangement.spacedBy(10.dp), // 设置行间距
//        horizontalArrangement = Arrangement.spacedBy(10.dp) // 设置列间距
//    ) {
//        items(mediaList.size) { index ->
//            val item = mediaList[index]
//            MediaItem(item)
//        }
//    }
}

@Composable
fun MediaItem(blog: BlogPartDto) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .paint(
//                painter = rememberAsyncPainter(url = "https://img.keaitupian.cn/uploads/2020/12/08/38d0befdc3c89348d6eeaed90c9b7660.jpg"),
//                contentScale = ContentScale.Crop
//            ),
//    ) {
//
//    }
}