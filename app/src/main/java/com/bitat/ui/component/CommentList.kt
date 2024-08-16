package com.bitat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.bitat.repository.dto.resp.CommentPart1Dto
import com.bitat.repository.dto.resp.CommentPartDto
import com.bitat.repository.store.CommentStore
import com.bitat.ui.theme.Typography
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@Composable
fun CommentList(blogId: Long) {
    val comments = remember {
        CommentStore.comments
    }

    LaunchedEffect(Dispatchers.Default) {
        CommentStore.initComments(blogId = blogId)
    }

    LazyColumn(modifier = Modifier.height(ScreenUtils.screenHeight.times(0.8).dp)) {
        items(comments) { item ->
            CommentItem(comment = item)
        }
    }
}

@Composable
fun CommentItem(comment: CommentPartDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(url = comment.profile, size = 32)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
            ) {
                Text(text = comment.nickname, style = Typography.bodySmall.copy(color = Color.Gray))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = TimeUtils.timeToText(comment.createTime),
                        style = Typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(" · ", style = Typography.bodySmall.copy(color = Color.Gray))
                    Text(comment.ipTerritory, style = Typography.bodySmall.copy(color = Color.Gray))
                }
            }
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        }
        Surface(modifier = Modifier.padding(start = 52.dp)) {
            Text(comment.content, style = Typography.bodyMedium.copy(lineHeight = 16.sp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { /*TODO*/ }) {
                Text("回复")
            }
        }
    }
}