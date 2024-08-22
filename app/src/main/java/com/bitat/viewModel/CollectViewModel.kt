package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.BlogOpsAddCollectDto
import com.bitat.repository.dto.req.BlogOpsRemoveCollectDto
import com.bitat.repository.dto.req.CollectNextListDto
import com.bitat.repository.dto.req.CreateCollectDto
import com.bitat.repository.dto.req.FindCollectOpusDto
import com.bitat.repository.dto.resp.CollectPartDto
import com.bitat.repository.http.service.BlogOpsReq
import com.bitat.repository.http.service.UserExtraReq
import com.bitat.state.CollectState
import com.bitat.ui.profile.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectViewModel : ViewModel() {
    private val _collectState = MutableStateFlow(CollectState())
    val collectState: StateFlow<CollectState> get() = _collectState.asStateFlow()

    fun updateBlog(blog: BlogBaseDto?) {
        _collectState.update {
            it.copy(currentBlog = blog)
        }
    }

    fun selectCollection(collection: CollectPartDto) {
        _collectState.update {
            it.copy(currentCollection = collection)
        }
    }

    fun initMyCollections() {
        MainCo.launch {
            UserExtraReq.collectList().await().map { res ->
                _collectState.update {
                    it.collections.clear()
                    it.collections.addAll(res)
                    it
                }
            }
        }
    }

    fun getDefaultCollection(isReload: Boolean = false, lastId: Long = 0) {
        MainCo.launch {
            UserExtraReq.findCollectOpus(
                FindCollectOpusDto(
                    pageSize = 20,
                    lastTime = lastId
                )
            ).await().map { res ->
                _collectState.update {
                    it.currentCollectionItems.clear()
                    it.currentCollectionItems.addAll(res)
                    it
                }
            }.errMap {
                println("there is an error ${it.msg}")
            }
        }
    }

    fun getBlogInCollection(key: Int) {
        MainCo.launch {
            UserExtraReq.collectNextList(CollectNextListDto(key = key, pageSize = 20, pageNo = 0))
                .await().map { res ->
                    _collectState.update {
                        it.currentCollectionItems.clear()
                        it.currentCollectionItems.addAll(res)
                        it
                    }
                }
        }
    }

    fun collectBlog(collectionKey: Int, completeFn: () -> Unit = {}) {
        MainCo.launch {
            if (collectState.value.currentBlog != null) {
                BlogOpsReq.addCollect(
                    BlogOpsAddCollectDto(
                        blogId = collectState.value.currentBlog!!.id,
                        key = collectionKey
                    )
                ).await().map {
                    completeFn()
                }
            }
        }
    }

    fun cancelCollect(completeFn: () -> Unit = {}) {
        MainCo.launch {
            BlogOpsReq.removeCollect(BlogOpsRemoveCollectDto(blogId = collectState.value.currentBlog!!.id))
                .await().map {
                    completeFn()
                }
        }
    }

    fun createCollection(collectionName: String, completeFn: () -> Unit = {}) {
        MainCo.launch {
            UserExtraReq.createCollect(CreateCollectDto(collectionName)).await().map {
                CuLog.error(CuTag.Blog, "collect create success")
                completeFn()
            }.errMap {
                CuLog.error(CuTag.Blog, "collect ${it.msg}")
            }

        }
    }
}