package com.bitat.ui.common

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bitat.ui.component.ImageBanner

@Composable
fun PicturePreview(uri: Uri, nextStep: (Uri) -> Unit) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(paddingValues = innerPadding)
        ) {
            AsyncImage(
                model = uri,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                contentDescription = null,
                contentScale = ContentScale.FillWidth //宽度撑满
            )
            IconButton(onClick = { nextStep(uri) }) {
                SvgIcon(
                    path = "svg/arrow-left.svg",
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}