package com.bitat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bitat.ext.clickableWithoutRipple

/**
 *    author : shilu
 *    date   : 2024/8/19  15:33
 *    desc   :
 */

@Composable
fun OperationTipsPop(visible: Boolean, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.size(150.dp).clickableWithoutRipple {
            onClose()
        }, contentAlignment = Alignment.BottomCenter) {
            AnimatedVisibility(visible = visible,
                enter = slideInVertically(animationSpec = tween(150), initialOffsetY = { it }),
                exit = slideOutVertically(animationSpec = tween(150), targetOffsetY = { it })) {

                Column(modifier = Modifier.size(150.dp).background(Color.Black).padding(30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Person,
                        contentDescription = "",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "操作成功!",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White))
                }
            }
        }
    }
}


