package com.bitat.state

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.bitat.repository.dto.resp.CommentPart2Dto
import com.bitat.repository.dto.resp.CommentPartDto
import com.bitat.repository.dto.resp.SubCommentPartDto
import com.bitat.repository.dto.resp.UserBase1Dto

data class CommentState(
    val currentBlogId: Long = -1,
    val lastCommentId: Long = 0,
    val comments: SnapshotStateList<CommentPartDto> = mutableStateListOf(),
    val subComments: SnapshotStateMap<Long, SnapshotStateList<SubCommentPartDto>> = mutableStateMapOf(),
    val commentCount: Int = 0,
    val commentInput: String = "",
    val replyComment: CommentPartDto? = null,
    val atUserSearchResult: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    val ats: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    val imagePath: Uri = Uri.EMPTY
)