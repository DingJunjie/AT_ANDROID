package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.MainCo
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.RecommendSearchDetailDto
import com.bitat.repository.dto.req.SearchCommonDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.toBlogBaseDto
import com.bitat.repository.http.service.SearchReq
import com.bitat.repository.po.WatchHistoryPo
import com.bitat.repository.sqlDB.WatchHistoryDB
import com.bitat.repository.store.UserStore
import com.bitat.state.ReelState
import com.bitat.state.ReelType
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf


class ReelViewModel : ViewModel() {
    private val _state = MutableStateFlow((ReelState()))
    val state: StateFlow<ReelState> get() = _state.asStateFlow()


    fun setCurrentBlog(blog: BlogBaseDto) {
        _state.update {
            it.copy(currentBlog = blog)
        }
    }

    fun refreshCurrent(currentBlog: BlogBaseDto) {
        MainCo.launch {
            if (_state.value.resList.size > 0) {
                val resListIndex = _state.value.resList.indexOf(currentBlog)
                if (resListIndex >= 0) _state.update {
                    it.resList[resListIndex] = currentBlog
                    it
                }
            } //            _state.update {
            //                it.copy(currentBlog = currentBlog)
            //            }
            _state.update {
                it.copy(flag = _state.value.flag + 1)
            }
        }
    }

    fun getList(isInit: Boolean, successFn: () -> Unit) {
        MainCo.launch {

            when (state.value.pageType) {
                ReelType.BLOG -> {
                    state.value.currentBlog?.let { item ->
                        if (isInit) {
                            _state.update {
                                it.resList.clear()
                                it
                            }
                            _state.update {
                                it.resList.add(item)
                                it
                            }
                            addWatchHistory(item)

                        }
                        SearchReq.recommendSearchDetail(RecommendSearchDetailDto(blogId = item.id))
                            .await()
                            .map { data ->
                                _state.update {
                                    it.resList.addAll(data)
                                    it
                                }
                                successFn()
                            }.errMap {
                                CuLog.debug(
                                    CuTag.Blog,
                                    "recommendSearchDetail failed ，code：${it.code}  msg:${it.msg}"
                                )
                            }
                    }
                }

                ReelType.SEARCH -> addPart()
                ReelType.COLLECT -> addPart()
                ReelType.LIKE -> addPart()
                ReelType.PHOTO -> addPart()
                ReelType.HISTORY -> addPart()
            }

        }
    }

    private fun addPart(list: Array<BlogPartDto> = arrayOf()) {
        val blogList = mutableListOf<BlogBaseDto>()
        if (list.isNotEmpty()) {

            list.forEach { part ->
                blogList.add(part.toBlogBaseDto())
            }
            _state.update {
                it.resList.addAll(blogList)
                it
            }
        } else {
            _state.value.blogPartList.forEach { part ->
                blogList.add(part.toBlogBaseDto())
            }
            _state.update {
                it.resList.clear()
                it.resList.addAll(blogList)
                it
            }
        }
    }

    fun setIndex(index: Int) {
        _state.update {
            it.copy(resIndex = index)
        }
    }

    fun reset() {
        _state.update { it.copy(resIndex = 0, currentBlog = null, pageType = ReelType.BLOG) }
        _state.update {
            it.resList.clear()
            it.blogPartList.clear()
            it
        }
    }

    fun likeClick(blog: BlogBaseDto) {
        blog.hasPraise = !blog.hasPraise
        blog.agrees = if (blog.hasPraise) blog.agrees + 1u else blog.agrees - 1u
        refreshCurrent(blog)
    }

    fun collectClick(blog: BlogBaseDto) {
        blog.hasCollect = !blog.hasCollect
        blog.collects = if (blog.hasCollect) blog.collects + 1u else blog.collects - 1u
        refreshCurrent(blog)
    }

    fun addWatchHistory(dto: BlogBaseDto) {
        CuLog.error(CuTag.Blog, "添加观看历史")
        viewModelScope.launch(Dispatchers.IO) {
//            WatchHistoryDB.insertOne(UserStore.userInfo.id,dto.kind.toShort(), dataId = dto.id,System.currentTimeMillis())
            WatchHistoryDB.insertOne(WatchHistoryPo().apply {
                userId = UserStore.userInfo.id
                kind = 1
                dataId = dto.id
                time = TimeUtils.getNow()
            })
        }
    }

    fun setSearchList(searchList: List<BlogPartDto>) {
        _state.update {
            it.blogPartList.addAll(searchList)
            it
        }
    }

    fun setPageType(type: ReelType) {
        _state.update { it.copy(pageType = type) }
    }

    fun getVideoSearchList(keyword: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            SearchReq.searchVideo(SearchCommonDto(keyword = keyword, pageNo = 0, pageSize = 20))
                .await().map { videos ->

                    addPart(videos)
                    onComplete()
                }.errMap {
                    CuLog.error(
                        CuTag.Blog,
                        "reel SearchReq searchVideo error code:${it.code},msg:${it.msg}"
                    )
                }
        }
    }

    fun setKevWords(keyWords: String) {
        _state.update {
            it.copy(searchKeyWords = keyWords)
        }
    }

}