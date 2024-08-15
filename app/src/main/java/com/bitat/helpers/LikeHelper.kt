package com.bitat.helpers

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.DelayMerge
import kotlinx.coroutines.launch

class LikeOperation(val blogId: Int, var like: Boolean) {
    var preLike: Boolean = like
        private set

    init {
        preLike = like
    }
}

class LikeHelper {
    companion object {
        var likeOperationDM: DelayMerge<LikeOperation>? = null
        var currentBlogId: Int = -1
        var likeOps: LikeOperation? = null
    }

    suspend fun like(
        blogId: Int,
        isLike: Boolean,
        likeFunc: suspend () -> Boolean,
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
                        println("request")
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
}