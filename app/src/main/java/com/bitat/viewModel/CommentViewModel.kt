package com.bitat.viewModel

import androidx.collection.intListOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.dto.req.BlogCreateCommentDto
import com.bitat.repository.dto.req.BlogCreateSubCommentDto
import com.bitat.repository.dto.req.BlogFindCommentDto
import com.bitat.repository.dto.req.BlogFindSubCommentDto
import com.bitat.repository.dto.resp.CommentPartDto
import com.bitat.repository.http.service.CommentReq
import com.bitat.state.CommentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommentViewModel : ViewModel() {
    private val _commentState = MutableStateFlow(CommentState())
    val commentState: StateFlow<CommentState> get() = _commentState.asStateFlow()

    suspend fun getComment(isReload: Boolean = true, pageSize: Int = 10) {
        if (isReload) {
            reset()
        }
        CommentReq.find(
            BlogFindCommentDto(
                blogId = commentState.value.currentBlogId,
                pageSize = pageSize,
                lastId = commentState.value.lastCommentId
            )
        ).await().map { res ->
            _commentState.update {
                it.apply {
                    it.comments.addAll(res.commentVec)
                }
            }
        }
    }


    fun updateBlogId(blogId: Long) {
        _commentState.update {
            it.copy(currentBlogId = blogId)
        }
    }

    fun updateComment(comment: String) {
        _commentState.update {
            it.copy(commentInput = comment)
        }
    }

    fun selectReplyComment(comment: CommentPartDto?) {
        _commentState.update {
            it.copy(replyComment = comment)
        }
    }

    suspend fun getSubComment(commentId: Long, pageSize: Int = 1) {
        var lastId: Long = 0;
        if (commentState.value.subComments[commentId]?.isNotEmpty() == true) {
            lastId = commentState.value.subComments[commentId]?.last()?.id ?: 0
        }
        CommentReq.findSub(
            BlogFindSubCommentDto(
                pId = commentId,
                pageSize = pageSize,
                lastId = lastId
            )
        ).await().map { res ->
            _commentState.update {
                it.apply {
                    if (it.subComments[commentId].isNullOrEmpty()) it.subComments[commentId] =
                        mutableStateListOf()
                    it.subComments[commentId]!!.addAll(res)
                }
            }
        }
    }

    suspend fun createComment(completeFn: () -> Unit) {
        CommentReq.create(
            BlogCreateCommentDto(
                commentState.value.currentBlogId,
                commentState.value.commentInput,
                labels = intArrayOf(),
                atUsers = arrayOf(),
                resource = ResourceDto()
            )
        ).await().map {
            // 提示评论成功，同时更新ui
            CuLog.info(CuTag.Comment, "发送评论成功")
            resetInput()
            completeFn()
            delay(1000L).let {
                getComment(isReload = true)
            }
        }.errMap {
            // 提示评论失败
            CuLog.error(CuTag.Comment, "发送评论失败, ${it.msg}")
        }
    }

    suspend fun createSubComment(completeFn: () -> Unit) {
        CommentReq.createSub(
            BlogCreateSubCommentDto(
                commentState.value.currentBlogId,
                commentState.value.replyComment!!.id,
                toUserId = commentState.value.replyComment!!.userId,
                content = commentState.value.commentInput,
                resource = ResourceDto(),
                atUsers = arrayOf(),
                labels = intArrayOf()
            )
        ).await().map {
            //
            CuLog.info(CuTag.Comment, "回复${commentState.value.replyComment!!.nickname}的评论成功")
            resetInput()
            completeFn()
        }.errMap {
            CuLog.error(CuTag.Comment, "回复评论失败${it.msg}")
        }
    }

    private fun reset(isResetBlog: Boolean = false) {
        _commentState.update {
            it.copy(
                comments = mutableStateListOf(),
                subComments = mutableStateMapOf(),
                lastCommentId = 0,
                currentBlogId = if (isResetBlog) -1 else it.currentBlogId
            )
        }
    }

    private fun resetInput() {
        _commentState.update {
            it.copy(
                commentInput = "",
                replyComment = null
            )
        }
    }
}