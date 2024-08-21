package com.bitat.ui.component

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import com.bitat.ui.common.rememberAsyncPainter

@Composable
fun Avatar(url: String, modifier: Modifier = Modifier, size: Int = 40) {
    Surface(
        shape = CircleShape, modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .size(size.dp)
                .border(width = size.dp, color = Color.Transparent, shape = CircleShape)
                .paint(painter = rememberAsyncPainter(url), contentScale = ContentScale.Crop)
        ) {
        }
    }
}


