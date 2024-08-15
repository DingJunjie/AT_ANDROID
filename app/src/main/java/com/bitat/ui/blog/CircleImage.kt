package com.bitat.ui.blog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.ui.common.SvgIcon

/****
 * 首页的头部item图片显示
 */
@Composable
fun CircleImage(imageUrl: String,modifier: Modifier=Modifier) {

    Box(
        modifier
            .size(40.dp)
            .padding(top = 7.dp, start = 5.dp)
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(35.dp),
                placeholder = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painterResource(R.drawable.logo), contentDescription = "头像", modifier = Modifier
//            .clip(RoundedCornerShape(8.dp))
                    .clip(CircleShape)
                    .size(35.dp), contentScale = ContentScale.Crop
            )
        }

        SvgIcon(
            path = "navigationbar/bottom_add.svg",
            tint = Color.Black,
            contentDescription = "",
            modifier = Modifier
                .size(15.dp)
                .offset(x = 24.dp, y = 20.dp)
        )

    }

}




