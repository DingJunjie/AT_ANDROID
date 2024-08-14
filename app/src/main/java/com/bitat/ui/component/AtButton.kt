package com.bitat.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.Typography

@Composable
fun AtButton(count: String, modifier: Modifier = Modifier, tapFn: () -> Unit) {
    Row(modifier = modifier
        .clickable { tapFn() }) {
        SvgIcon(
            modifier = Modifier.size(20.dp),
            path = "svg/at_bold.svg",
            tint = Color.Black,
            contentDescription = ""
        )
        Text(
            count, style = Typography.bodyMedium.copy(
                fontSize = 10.sp,
                color = Color.Black
            )
        )
    }
}