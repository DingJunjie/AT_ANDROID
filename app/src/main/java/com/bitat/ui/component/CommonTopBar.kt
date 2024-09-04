package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitat.repository.dto.resp.CollectPartDto
import com.bitat.ui.common.statusBarHeight
import com.google.android.material.badge.BadgeDrawable.BadgeGravity

@Composable
fun CommonTopBar(modifier: Modifier = Modifier, title: String, tint: Color = Color.Black, backFn: () -> Unit, isBg: Boolean = false, bgColor: Color = Color.White, padingStatus: Boolean = false, endButton: @Composable () -> Unit = {
    Box(modifier = Modifier.size(30.dp)) {}
}) {
    Column {
        if (padingStatus) Spacer(modifier = Modifier.fillMaxWidth().height(statusBarHeight).background(if (isBg) bgColor else Color.Transparent))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .background(if (isBg) bgColor else Color.Transparent)) {
            BackButton(tint = tint) {
                backFn()
            }
            Text(title)
            endButton()
        }
    }

}