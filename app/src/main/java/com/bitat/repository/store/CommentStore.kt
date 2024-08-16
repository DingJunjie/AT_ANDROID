package com.bitat.repository.store

import com.bitat.MainCo
import com.bitat.repository.dto.req.BlogFindCommentDto
import com.bitat.repository.dto.resp.CommentPart2Dto
import com.bitat.repository.dto.resp.CommentPartDto
import com.bitat.repository.http.service.BlogOpsReq
import com.bitat.repository.http.service.CommentReq
import kotlinx.coroutines.launch

object CommentStore {
    var currentBlogId: Long = -1
    var comments: MutableList<CommentPartDto> = mutableListOf()

    fun initComments(blogId: Long, lastId: Long = 0, pageSize: Int = 20) {
        MainCo.launch {
            CommentReq.find(BlogFindCommentDto(blogId, lastId, pageSize)).await().map {
                if (lastId == 0.toLong()) {
                    comments.clear()
                }
                comments.addAll(it.commentVec)
            }
        }
    }


}