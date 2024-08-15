package com.bitat.ui.component

import android.annotation.SuppressLint
import androidx.collection.intListOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.MainCo
import com.bitat.helpers.LikeHelper
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun LikeButton(
    id: Long,
    count: String,
    isLiked: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    tapFn: () -> Unit
) {
    val likeHelper = LikeHelper()
    val currentLike = remember {
        mutableStateOf(isLiked)
    }
    Row(modifier = modifier.clickable {
        currentLike.value = !currentLike.value
        MainCo.launch {
            likeHelper.like(id, isLike = currentLike.value, {
                likeHelper.likeById(id, currentLike.value, listOf()) {
                    tapFn()
                }
            }, {

            })
        }
        tapFn()
    }) {
        SvgIcon(
            modifier = Modifier.size(20.dp),
            path = if (currentLike.value) "svg/like_fill.svg" else "svg/like_line.svg",
            tint = Color.Black,
            contentDescription = ""
        )
        Text(
            count, style = Typography.bodyMedium.copy(
                fontSize = 10.sp, color = Color.Black
            )
        )
    }
}