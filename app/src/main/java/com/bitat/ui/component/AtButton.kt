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
import com.bitat.ext.toAmountUnit
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.Typography

@Composable
fun AtButton(count: Int, modifier: Modifier = Modifier, tintColor: Color=Color.Black,tapFn: () -> Unit) {
    Row(modifier = Modifier
        .clickable { tapFn() }) {
        SvgIcon(
            modifier = modifier,
            path = "svg/ate.svg",
            tint = tintColor,
            contentDescription = ""
        )
        if (count > 0) Text(
            count.toAmountUnit(), style = Typography.bodyMedium.copy(
                fontSize = 10.sp,
                color = tintColor
            )
        )
    }
}