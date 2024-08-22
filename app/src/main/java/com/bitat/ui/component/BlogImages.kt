package com.bitat.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.TotalIndexShow
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ImagePreviewViewModel

@Composable
fun BlogImages(
    dto: BlogBaseDto,
    maxHeight: Int,
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[ImagePreviewViewModel::class]
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.Start,
    ) {
        ImageBox(dto, maxHeight, navHostController) {
//            Toast
//                .makeText(context, data, Toast.LENGTH_SHORT)
//                .show()
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
    maxHeight: Int,
    navHostController: NavHostController,
    onClic: (Int) -> Unit
) {

    val dataList = blog.resource.images


    LazyRow(
        modifier = Modifier.fillMaxWidth(),
//        contentPadding = PaddingValues(start = ScreenUtils.screenWidth.times(0.11).dp)
    ) {
        itemsIndexed(dataList) { index, data ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onClic(index)
                    }
                    .padding(3.dp)
                    .background(Color.Transparent),
            ) {
                if (data.isNotEmpty()) {

                    //获得屏幕的宽度
                    val wrapperWidth = ScreenUtils.screenWidth * 0.88 - 10

                    Box {
                        AsyncImage(
                            model = data,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .width(wrapperWidth.dp)
                                .height(maxHeight.dp)
                                .background(Color.Transparent),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = (wrapperWidth - 30).dp, top = 5.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            if (dataList.size > 1) {
                                TotalIndexShow((index + 1).toString(), dataList.size.toString())
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