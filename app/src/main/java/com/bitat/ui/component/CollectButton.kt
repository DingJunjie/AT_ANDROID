package com.bitat.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bitat.ui.common.SvgIcon

@Composable
fun CollectButton(
    hasCollect: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    tapFn: () -> Unit
) {
    SvgIcon(
        modifier = modifier.clickable {
            tapFn()
        },
        path = "svg/collection.svg",
        tint = Color.Black,
        contentDescription = ""
    )
}