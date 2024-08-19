package com.bitat.ui.blog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 *    author : shilu
 *    date   : 2024/8/19  11:49
 *    desc   :
 */

@Composable
fun BlogMorePop(visible: Boolean, blogId: Long, onClose: () -> Unit) {
    com.bitat.ui.component.Popup(visible = visible, onClose = onClose) {
        Card(backgroundColor = Color.White) {
            Column {
                Text("艾特")
                Text("艾特与世界会有，快去关注吧~")
                Column {
                    Text("不感兴趣")
                    Text("二维码")
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    Text("举报")
                    Text("拉黑")
                }
            }


        }
    }
}