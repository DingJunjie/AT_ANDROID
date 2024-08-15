package com.bitat.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import com.bitat.repository.dto.resp.CommentPartDto

@Composable
fun CommentList(comments: List<CommentPartDto>) {
    LazyColumn {
        items(comments) { item ->
            CommentItem(comment = item)
        }
    }
}

@Composable
fun CommentItem(comment: CommentPartDto) {
    Column {
        Row {
            Avatar(url = comment.profile, size = 20)
            Column {
                Text(text = comment.nickname)
                Row(modifier = Modifier.weight(1f)) {
                    Text(text = comment.createTime.toString())
                    Text(" · ")
                    Text(comment.ipTerritory)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.Add, contentDescription = "")
                }
            }
        }
        Surface {
            Text(comment.content)
        }
        Row {
            TextButton(onClick = { /*TODO*/ }) {
                Text("回复")
            }
        }
    }
}