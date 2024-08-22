package com.bitat.ui.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil.ImageLoader
import coil.compose.AsyncImage
import com.bitat.MainCo
import com.bitat.ext.clickableWithoutRipple
import com.bitat.repository.dto.resp.CommentPart1Dto
import com.bitat.repository.dto.resp.CommentPartDto
import com.bitat.repository.dto.resp.SubCommentPartDto
import com.bitat.repository.store.UserStore
import com.bitat.state.CommentState
import com.bitat.ui.common.AnyPopDialog
import com.bitat.ui.common.WeDialog
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
    tapImage: (String) -> Unit,
    removeComment: (CommentPartDto) -> Unit = {},
    removeSubComment: (SubCommentPartDto, Long, Long) -> Unit,
    tapContentFn: (CommentPartDto?) -> Unit,
    subCommentTap: (SubCommentPartDto?) -> Unit
) {
    commentViewModel.updateBlogId(blogId)

    LaunchedEffect(Dispatchers.Default) {
        commentViewModel.getComment(isReload = true)
    }

    LaunchedEffect(commentState.subComments) {
        println("this one changed ${commentState.subComments}")
    }

    LazyColumn(modifier = Modifier
        .height(if (commentState.comments.size>0)ScreenUtils.screenHeight.times(0.5).dp else 0.dp)
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
                tapImage = tapImage,
                tapSubCommentMore = {
                    MainCo.launch {
                        commentViewModel.getSubComment(item.id)
                    }
                },
                removeComment = removeComment,
                removeSubComment = {
                    removeSubComment(it, item.blogId, item.id)
                },
                subCommentTap = subCommentTap,
                tapFn = tapContentFn
            )
        }
    }
}

@Composable
fun SubCommentList(
    subComments: List<SubCommentPartDto>,
    removeSubComment: (SubCommentPartDto) -> Unit = {},
    subCommentTap: (SubCommentPartDto?) -> Unit
) {
    if (subComments.isEmpty()) Box {}
    else {
        Surface(
            modifier = Modifier
                .padding(start = 50.dp)
                .background(Color.LightGray)
        ) {
            Column {
                subComments.forEach { item ->
                    SubCommentItem(
                        item,
                        subCommentTap = subCommentTap,
                        removeSubComment = removeSubComment
                    )
                }
            }
        }
    }
}

@Composable
fun SubCommentItem(
    subComment: SubCommentPartDto,
    subCommentTap: (SubCommentPartDto?) -> Unit,
    removeSubComment: (SubCommentPartDto) -> Unit = {},
) {
    val deleteShow = remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    deleteShow.value = true
                }, onTap = {
                    subCommentTap(subComment)
                })
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(subComment.nickname + " :")
            Text(subComment.content)
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.Add, contentDescription = "")
        }
    }

    if (deleteShow.value && subComment.userId == UserStore.userInfo.id) {
        WeDialog(title = "删除评论", content = "是否删除该评论", onOk = {
            removeSubComment(subComment)
            deleteShow.value = false
        }, okColor = Color.White, onDismiss = {
            deleteShow.value = false
        })
    }
}

@Composable
fun CommentItem(
    comment: CommentPartDto,
    subComments: List<SubCommentPartDto>,
    removeComment: (CommentPartDto) -> Unit,
    removeSubComment: (SubCommentPartDto) -> Unit = {},
    tapImage: (String) -> Unit,
    tapSubCommentMore: () -> Unit,
    tapShowSubComment: (Long) -> Unit,
    subCommentTap: (SubCommentPartDto?) -> Unit,
    tapFn: (CommentPartDto) -> Unit
) {
    val isShowSub = remember {
        mutableStateOf(false)
    }

    val deleteShow = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    deleteShow.value = true
                })
            }
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
        Column(modifier = Modifier
            .padding(start = 52.dp)
            .clickable {
                tapFn(comment)
            }) {
            if (comment.resource.images.isNotEmpty()) {
                CommentImage(image = comment.resource.images.first()) {
                    tapImage(it)
                }
            }
            Text(comment.content, style = Typography.bodyMedium.copy(lineHeight = 16.sp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {
                tapFn(comment)

            }) {
                Text("回复")
            }
        }
        if (isShowSub.value && subComments.isNotEmpty()) {
            Column {
                SubCommentList(subComments, removeSubComment, subCommentTap)
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



    if (deleteShow.value && comment.userId == UserStore.userInfo.id) {
        WeDialog(title = "删除评论", content = "是否删除该评论", onOk = {
            removeComment(comment)
            deleteShow.value = false
        }, okColor = Color.White, onDismiss = {
            deleteShow.value = false
        })
    }
}

@Composable
fun CommentImage(image: String, tapFn: (String) -> Unit) {
    Box(modifier = Modifier
        .size(60.dp)
        .clickable {
            tapFn(image)
        }) {
        AsyncImage(
            model = image,
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )
    }
}