package com.bitat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.repository.dto.resp.CollectPartDto

@Composable
fun CommonTopBar(
    title: String,
    backFn: () -> Unit,
    endButton: @Composable () -> Unit = { Box(modifier = Modifier.size(30.dp)) {} }
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        BackButton {
            backFn()
        }
        Text(title)
        endButton()
    }
}