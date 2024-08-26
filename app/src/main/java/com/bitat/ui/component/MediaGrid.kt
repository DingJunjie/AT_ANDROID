package com.bitat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bitat.repository.dto.resp.BlogPartDto
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
            Box(modifier = Modifier.width(width.dp).height(height.dp).padding(1.dp)) {
                if (it.cover.isNotEmpty()) {
                    AsyncImage(model = it.cover.ifEmpty {
                        if (it.resource.images.isNotEmpty()) {
                            it.resource.images.first()
                        }
                    }, contentDescription = "", contentScale = ContentScale.Crop)
                } else {
                    AsyncImage(model = "https://file.bitebei.com/i_u_126&t_1720077862743&x_1280&y_1920?imageView2/2/w/900/&e=1724682138&token=Cb1TU1t1vOR7LDfWtMkBlAMbb7AjXe4HYXALbHO4:ZwcbYIEynxB1MBSk7XBbNdnvnfQ=",
                        contentDescription = "",
                        contentScale = ContentScale.Crop)
                }
                Icon(Icons.Filled.AccountBox,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }

    //    LazyVerticalGrid(modifier = Modifier.fillMaxSize(),columns = GridCells.Fixed(3), // 设置固定列数为2
    //        contentPadding = PaddingValues(16.dp), // 设置内边距
    //
    //        verticalArrangement = Arrangement.spacedBy(10.dp), // 设置行间距
    //        horizontalArrangement = Arrangement.spacedBy(10.dp) // 设置列间距
    //    ) {
    //        items(mediaList.size) { index ->
    //            val item = mediaList[index]
    //            MediaItem(item)
    //
    //        }
    //    }
}

@Composable
fun MediaItem(blog: BlogPartDto) {

    Box(modifier = Modifier.padding(1.dp) // 保持每个网格项为正方形
    ) {
        if (blog.cover.isNotEmpty()) {
            AsyncImage(model = blog.cover.ifEmpty {
                if (blog.resource.images.isNotEmpty()) {
                    blog.resource.images.first()
                }
            }, contentDescription = "", contentScale = ContentScale.Crop)
        } else {
            AsyncImage(model = "https://file.bitebei.com/i_u_126&t_1720077862743&x_1280&y_1920?imageView2/2/w/900/&e=1724682138&token=Cb1TU1t1vOR7LDfWtMkBlAMbb7AjXe4HYXALbHO4:ZwcbYIEynxB1MBSk7XBbNdnvnfQ=",
                contentDescription = "",
                contentScale = ContentScale.Crop)
        }
        Icon(Icons.Filled.AccountBox,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.align(Alignment.TopEnd))
    }
}