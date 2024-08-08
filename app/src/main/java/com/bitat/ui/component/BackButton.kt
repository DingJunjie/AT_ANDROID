package com.bitat.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.ui.common.SvgIcon

@Composable
fun BackButton(tapFn: () -> Unit) {
    IconButton(onClick = tapFn) {
        SvgIcon(
            path = "svg/arrow-left.svg",
            contentDescription = "",
            modifier = Modifier.size(20.dp)
        )
    }
}