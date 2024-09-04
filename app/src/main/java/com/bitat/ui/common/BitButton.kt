package com.bitat.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/2  12:02
 *    desc   : 防抖btn
 */
@Composable
fun DebouncedButton(onClick: () -> Unit, modifier: Modifier = Modifier, debounceTime: Long = 500L, // 防抖时间，默认为 500ms
    content: @Composable RowScope.() -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    Button(onClick = {
        if (enabled) {
            onClick()
            enabled = false
            coroutineScope.launch {
                delay(debounceTime)
                enabled = true
            }
        }
    }, modifier = modifier.fillMaxWidth(), enabled = enabled) {
        content()
    }
}