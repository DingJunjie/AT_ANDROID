package com.bitat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bitat.repository.po.SingleMsgPo
import com.bitat.utils.ScreenUtils


@Composable
fun SenderMessage(msg: SingleMsgPo) {
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
//                .fillMaxWidth(0.6f)
                .widthIn(max = ScreenUtils.screenWidth.times(0.6).dp, min = 10.dp)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            Text(
                text = msg.content,
                color = Color.White,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
            )
        }
    }
}

@Composable
fun RecipientMessage(msg: SingleMsgPo) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Blue,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
//                .fillMaxWidth(0.6f)
                .widthIn(max = ScreenUtils.screenWidth.times(0.6).dp, min = 10.dp)
//                .background(Color.Black)
//                .height(100.dp),
        ) {
            Text(
                text = msg.content,
                color = Color.White,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp)
            )
        }
    }
}