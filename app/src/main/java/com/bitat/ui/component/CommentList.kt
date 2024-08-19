package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.bitat.MainCo
import com.bitat.ext.clickableWithoutRipple
import com.bitat.repository.dto.resp.CommentPart1Dto
import com.bitat.repository.dto.resp.CommentPartDto
import com.bitat.repository.dto.resp.SubCommentPartDto
import com.bitat.repository.store.CommentStore
import com.bitat.state.CommentState
import com.bitat.ui.theme.Typography
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.CommentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@Composable
fun CommentList(
    blogId: Long,
    commentViewModel: CommentViewModel,
    commentState: CommentState,
    tapContentFn: (CommentPartDto?) -> Unit
) {
    commentViewModel.updateBlogId(blogId)

    LaunchedEffect(Dispatchers.Default) {
        commentViewModel.getComment(isReload = true)
    }

    LaunchedEffect(commentState.subComments) {
        println("this one changed ${commentState.subComments}")
    }

    LazyColumn(
        modifier = Modifier
            .height(ScreenUtils.screenHeight.times(0.5).dp)
            .clickableWithoutRipple {
                tapContentFn(null)
            }) {
        items(commentState.comments) { item ->
            CommentItem(
                comment = item,
                subComments = commentState.subComments[item.id] ?: listOf(),
                tapShowSubComment = {
                    MainCo.launch {
                        commentViewModel.getSubComment(item.id)
                    }
                },
                tapSubCommentMore = {
                    MainCo.launch {
                        commentViewModel.getSubComment(item.id)
                    }
                },
                tapFn = tapContentFn
            )
        }
    }
}

@Composable
fun SubCommentList(subComments: List<SubCommentPartDto>) {
    if (subComments.isEmpty()) Box {}
    else {
        Surface(modifier = Modifier
            .padding(start = 50.dp)
            .background(Color.LightGray)) {
            Column {
                subComments.forEach { item ->
                    SubCommentItem(item)
                }
            }
        }
    }
}

@Composable
fun SubCommentItem(subComment: SubCommentPartDto) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(subComment.nickname + " :")
            Text(subComment.content)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.Add, contentDescription = "")
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentPartDto,
    subComments: List<SubCommentPartDto>,
    tapSubCommentMore: () -> Unit,
    tapShowSubComment: (Long) -> Unit,
    tapFn: (CommentPartDto) -> Unit
) {
    val isShowSub = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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
        Surface(modifier = Modifier
            .padding(start = 52.dp)
            .clickable {
                tapFn(comment)
            }) {
            Text(comment.content, style = Typography.bodyMedium.copy(lineHeight = 16.sp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { /*TODO*/ }) {
                Text("回复")
            }
        }
        if (isShowSub.value && subComments.isNotEmpty()) {
            Column {
                SubCommentList(subComments)
                if (subComments.size < comment.comments.toInt()) Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            tapSubCommentMore()
                        }, horizontalArrangement = Arrangement.Center
                ) {
                    Text("查看更多")
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "")
                }
            }
        } else if (comment.comments.toInt() > 0) Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    tapShowSubComment(comment.id).let { isShowSub.value = true }

                }, horizontalArrangement = Arrangement.Center
        ) {
            Text("查看${comment.comments}条回复")
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "")
        }
    }
}