package com.bitat.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitat.ui.theme.Typography

@Composable
fun DisableMessage(content: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = content,
            color = Color.Gray,
            style = Typography.bodySmall.copy(),
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
        )
    }
}