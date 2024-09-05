package com.bitat.ui.chat

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitat.ext.toChatMessage
import com.bitat.state.ReplyMessageParams

@Composable
fun SenderReplyMessage(message: String, replyContent: String, replyMsgKind: Short) {
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp),
            modifier = Modifier
                .padding(top = 10.dp, end = 20.dp)
                .fillMaxWidth(0.6f)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
            )
        }

        Surface(
            color = Color.Gray,
            shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp),
            modifier = Modifier
                .padding(bottom = 10.dp, end = 20.dp)
                .fillMaxWidth(0.6f)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            Text(
                text = "回复：${replyContent.toChatMessage(replyMsgKind)}",
                color = Color.White,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
            )
        }
    }
}

@Composable
fun RecipientReplyMessage(message: String, replyContent: String, replyMsgKind: Short) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp),
            modifier = Modifier
                .padding(top = 10.dp, end = 20.dp)
                .fillMaxWidth(0.6f)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
            )
        }

        Surface(
            color = Color.Gray,
            shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp),
            modifier = Modifier
                .padding(bottom = 10.dp, end = 20.dp)
                .fillMaxWidth(0.6f)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            Text(
                text = "回复：${replyContent.toChatMessage(replyMsgKind)}",
                color = Color.White,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
            )
        }
    }
}