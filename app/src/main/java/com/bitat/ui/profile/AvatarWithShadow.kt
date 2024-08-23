package com.bitat.ui.profile

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import com.bitat.ui.common.rememberAsyncPainter

@Composable
fun AvatarWithShadow(url: String, modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp)
            .shadow(elevation = 5.dp, shape = CircleShape)
    ) {
        Box(
            modifier = modifier
                .size(76.dp)
                .border(width = 38.dp, color = Color.Transparent, shape = CircleShape)
                .paint(painter = rememberAsyncPainter(url), contentScale = ContentScale.Crop)
        ) {

        }
    }
}