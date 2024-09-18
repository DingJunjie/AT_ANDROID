package com.bitat.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.state.CommentState
import com.bitat.viewModel.CommentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CommentPopup(
    visible: Boolean,
    commentViewModel: CommentViewModel,
    commentState: CommentState,
    coroutineScope: CoroutineScope,
    tapImage: (String) -> Unit,
    blogId: Long,
    onClose: () -> Unit,
    isPop: Boolean = true,
    commentSuccess: () -> Unit = {}
) {
    if (isPop) {
        Popup(visible = visible, onClose = { onClose() }) {
            CommonLayout(
                blogId,
                commentViewModel,
                commentState,
                coroutineScope,
                tapImage,
                commentSuccess
            )
        }
    } else {
        Column {
            CommonLayout(
                blogId,
                commentViewModel,
                commentState,
                coroutineScope,
                tapImage,
                commentSuccess
            )
        }
    }
}

@Composable
fun CommonLayout(
    blogId: Long,
    commentViewModel: CommentViewModel,
    commentState: CommentState,
    coroutineScope: CoroutineScope,
    tapImage: (String) -> Unit,
    commentSuccess: () -> Unit = {}
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(commentState.commentInput))
    }

    val focusRequester = remember {
        FocusRequester()
    }

    fun addAtUser(user: UserBase1Dto) {
        val result = commentViewModel.selectUser(user)
        if (textFieldValue.text.isEmpty()) {
            textFieldValue = textFieldValue.copy(text = "@${user.nickname} ")
            return
        }
        val textArr = textFieldValue.text.split("")
        val before = textArr.subList(0, textFieldValue.selection.start + 1)
        val afterStr = textArr.subList(textFieldValue.selection.start, textFieldValue.text.length)
            .joinToString("")

        val lastAtOffset = before.lastIndexOf("@")
        val beforeString = before.subList(0, lastAtOffset).joinToString("")

        val total = "$beforeString@${user.nickname}$afterStr "

        textFieldValue = textFieldValue.copy(text = total, selection = TextRange(total.length))

        commentViewModel.clearUserSearch()
    }

    val atStart = remember {
        mutableStateOf(-1)
    }

    val atEnd = remember {
        mutableStateOf(-1)
    }

    val inputAt = remember {
        mutableStateOf("")
    }

    fun onContentChange(content: String, cursorOffset: Int = -1) {
        if (cursorOffset == -1) return;
        if (content.isEmpty()) {
            atStart.value = -1
            return
        }

        val latestChar = content.split("")[cursorOffset]

        if (cursorOffset < atStart.value) {
            atStart.value = -1
            return
        }

        if (latestChar == "@" && atStart.value < 0) {
            atStart.value = cursorOffset - 1
            commentViewModel.searchUser("")
        } else if (latestChar == " " && atStart.value >= 0) {
            atEnd.value = cursorOffset - 1

            atStart.value = -1
            inputAt.value = ""
            commentViewModel.clearUserSearch()
        } else if (atStart.value >= 0) {
            inputAt.value = content.substring(atStart.value + 1, cursorOffset)
            commentViewModel.searchUser(inputAt.value)
        }
    }

    CommentList(blogId, commentViewModel, commentState, tapImage = tapImage, removeComment = {
        commentViewModel.removeComment(it)
    }, removeSubComment = { c, blogId, pId ->
        commentViewModel.removeSubComment(blogId, pId, c)
    }, subCommentTap = {
        commentViewModel.selectReplySubComment(it)
        focusRequester.requestFocus()
    }, tapContentFn = {
        commentViewModel.selectReplyComment(it)
        focusRequester.requestFocus()
    })

    Box(contentAlignment = Alignment.BottomCenter) {
        CommentTextField(textFieldValue, focusRequester = focusRequester, sendComment = {
            coroutineScope.launch {
                if (commentState.replyComment == null) {
                    commentViewModel.createComment {
                        textFieldValue = textFieldValue.copy(text = "")
                        commentSuccess()
                    }
                } else {
                    commentViewModel.createSubComment {
                        textFieldValue = textFieldValue.copy(text = "")
                        commentSuccess()
                    }
                }

            }
        }, placeholder = if (textFieldValue.text.isNotEmpty()) "" else {
            if (commentState.replyComment != null) "回复${commentState.replyComment.nickname}："
            else if (commentState.replySubComment != null) "回复${commentState.replySubComment.nickname}："
            else "请输入您的评论"
        }, atUsers = commentState.atUserSearchResult, selectUser = {
            addAtUser(it)
            atStart.value = -1
            inputAt.value = ""
        }, tapAt = {
            val b = textFieldValue.text.substring(0, textFieldValue.selection.start)
            val a = textFieldValue.text.substring(
                TextRange(
                    textFieldValue.selection.start,
                    textFieldValue.text.length
                )
            )
            val t = "$b@$a"
            focusRequester.requestFocus()
            textFieldValue = textFieldValue.copy(
                text = t,
                selection = TextRange(textFieldValue.selection.start + 1)
            )
            atStart.value = textFieldValue.selection.start
            commentViewModel.searchUser("")
        }, selectedImage = commentState.imagePath, imageSelect = {
            commentViewModel.selectImage(it)
        }, onValueChange = {
            commentViewModel.updateComment(it.text)
            textFieldValue = it
            onContentChange(it.text, it.selection.start)
        })

    }


}

