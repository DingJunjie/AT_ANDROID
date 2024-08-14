package com.bitat.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.ui.theme.Typography

@Composable
fun BlogText(content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(
            text = content, style = Typography.bodyLarge.copy(
                fontSize = 14.sp
            )
        )
    }
}