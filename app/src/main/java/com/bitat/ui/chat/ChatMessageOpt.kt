package com.bitat.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.bitat.repository.po.SingleMsgPo

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessageOpt(
    msg: SingleMsgPo,
    reply: () -> Unit = {},
    copy: () -> Unit = {},
    recall: () -> Unit = {},
    delete: () -> Unit = {}
) {
    val current = LocalClipboardManager.current

    Surface(modifier = Modifier.fillMaxWidth(0.6f), color = Color(0xcc333333)) {
        FlowRow(modifier = Modifier.padding(10.dp)) {
            OptItem(title = "回复", tapFn = reply) {
                Icon(Icons.Filled.Star, contentDescription = "")
            }
            OptItem(title = "复制", tapFn = {
                current.setText(AnnotatedString(msg.content))
                copy()
            }) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "")
            }
            OptItem(title = "删除", tapFn = delete) {
                Icon(Icons.Filled.Delete, contentDescription = "")
            }
            OptItem(title = "多选", tapFn = {}) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "")
            }
            OptItem(title = "撤回", tapFn = {
                recall()
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "")
            }
            OptItem(title = "艾特", tapFn = {}) {
                Icon(Icons.Filled.Create, contentDescription = "")
            }

        }
    }
}

@Composable
fun OptItem(title: String, tapFn: () -> Unit, button: @Composable () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth(0.24f)
        .clickable { tapFn() }) {
        button()
        Text(title)
    }
}