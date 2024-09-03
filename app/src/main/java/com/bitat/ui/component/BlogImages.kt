package com.bitat.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.TotalIndexShow
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ImagePreviewViewModel

@Composable
fun BlogImages(
    dto: BlogBaseDto,
    maxHeight: Int,
    needRoundedCorner: Boolean = true,
    needStartPadding: Boolean = true,
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[ImagePreviewViewModel::class]
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), //            .background(Color.Transparent)
        horizontalAlignment = Alignment.Start,
    ) {
        ImageBox(dto, needRoundedCorner, needStartPadding, maxHeight, navHostController) {
            vm.setImagePreView(dto.resource.images)
            AtNavigation(navHostController).navigateToImagePreviewPage()
        };
    }
    CuLog.info(CuTag.Blog, "BlogImage--------- ${dto.id}") //        ReelPage()
}

/**
 * 横向列表LazyRow
 */
@Composable
fun ImageBox(
    blog: BlogBaseDto,
    needRoundedCorner: Boolean = true,
    needStartPadding: Boolean = true,
    maxHeight: Int,
    navHostController: NavHostController,
    onClick: (Int) -> Unit
) {

    val dataList = blog.resource.images


    LazyRow(
        modifier = Modifier.fillMaxWidth(), //        contentPadding = PaddingValues(start = ScreenUtils.screenWidth.times(0.11).dp)
    ) {
        itemsIndexed(dataList) { index, data ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onClick(index)
                    }
                    .padding(
                        start = if (index == 0 && needStartPadding) ScreenUtils.screenWidth.times(
                            0.1
                        ).dp else 0.dp
                    )
                    .padding(
                        PaddingValues(all = 3.dp)
                    )
                    .background(Color.Transparent),
            ) {
                if (data.isNotEmpty()) {

                    //获得屏幕的宽度
                    val wrapperWidth =
                        if (needStartPadding) ScreenUtils.screenWidth
                            .times(0.88)
                            .minus(10).dp else ScreenUtils.screenWidth.dp

                    Box {
                        AsyncImage(model = data,
                            modifier = Modifier
                                .clip(RoundedCornerShape(if (needRoundedCorner) 8.dp else 0.dp))
                                .width(
                                    wrapperWidth
                                )
                                .height(maxHeight.dp)
                                .background(Color.Transparent),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            onState = { state ->
                                if (state is AsyncImagePainter.State.Success) {
                                    val width = state.painter.intrinsicSize.width
                                    val height =
                                        state.painter.intrinsicSize.height //                                    imageSize = Size(width.toInt(), height.toInt())
                                    CuLog.debug(
                                        CuTag.Blog,
                                        "图片高度计算 Image width: ${width.toInt()} px, height: ${height.toInt()} px,maxHeight:${maxHeight}"
                                    )
                                }
                            })
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, end = 10.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            if (dataList.size > 1) {
                                TotalIndexShow(
                                    modifier = Modifier.align(
                                        Alignment.TopEnd
                                    ), (index + 1).toString(), dataList.size.toString()
                                )
                            }

                        }
                    }
                } else {
                    Image(
                        painterResource(R.drawable.logo),
                        contentDescription = "默认图片",
                        modifier = Modifier //            .clip(RoundedCornerShape(8.dp))
                            .clip(CircleShape)
                            .size(50.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}