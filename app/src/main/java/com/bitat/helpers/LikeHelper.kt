package com.bitat.helpers

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.BlogAgreeDto
import com.bitat.repository.http.service.BlogOpsReq
import com.bitat.utils.DelayMerge
import kotlinx.coroutines.launch

class LikeOperation(val blogId: Long, var like: Boolean) {
    var preLike: Boolean = like
        private set

    init {
        preLike = like
    }
}

class LikeHelper {
    companion object {
        var likeOperationDM: DelayMerge<LikeOperation>? = null
        var currentBlogId: Long = -1
        var likeOps: LikeOperation? = null
    }

    suspend fun like(
        blogId: Long,
        isLike: Boolean,
        likeFunc: suspend () -> Unit,
        completeFunc: () -> Unit,
        errorFunc: (() -> Unit)? = null
    ) {
        try {
            if (likeOps == null) {
                likeOps = LikeOperation(blogId, isLike)
            }

            if (likeOperationDM == null) {
                likeOperationDM = DelayMerge { likeOperation ->
                    println(System.currentTimeMillis())
                    if (likeOps!!.preLike != likeOperation.like) {
                        MainCo.launch {
                            likeFunc()
                            currentBlogId = -1
                            completeFunc()
                        }
                    }
                    likeOperationDM = null
                    likeOps = null
                }
            }

            likeOps!!.like = !isLike
            likeOperationDM!!.invoke(blogId, likeOps!!)
        } catch (e: Exception) {
            CuLog.error(CuTag.Blog, e.message.toString())
            errorFunc?.invoke()
        }
    }

    suspend fun likeById(
        id: Long,
        isLike: Boolean,
        labels: List<Int>,
        completeFunc: () -> Unit
    ) {
        CuLog.info(CuTag.Blog, "点赞中，当前点赞为$isLike")
        BlogOpsReq.agree(
            BlogAgreeDto(
                blogId = id,
                labels = labels.toIntArray(),
                ops = if (isLike) 1 else 0
            )
        ).await().map {
            completeFunc()
        }
    }
}