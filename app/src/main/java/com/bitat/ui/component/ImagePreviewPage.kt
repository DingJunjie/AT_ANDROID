package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.TopBar

/**
 *    author : shilu
 *    date   : 2024/8/16  17:51
 *    desc   : 图片预览页面
 */

@Composable
fun ImagePreviewPage(imgs: Array<String>, navController: NavHostController) {
    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)
            .padding(paddingValues = innerPadding)) {

            LazyRow {
                items(imgs) { item ->
                    AsyncImage(model = item,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).fillParentMaxWidth()
                            .background(Color.Black),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth //宽度撑满
                    )
                }
            }
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(painterResource(R.drawable.nav_ait),
                    contentDescription = "backIcon",
                    modifier = Modifier.size(40.dp))
            }
        }

    }
}