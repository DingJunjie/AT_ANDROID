package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_IMAGES_ONLY
import com.bitat.repository.consts.BLOG_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.repository.dto.req.RecommendSearchDetailDto
import com.bitat.repository.http.service.SearchReq
import com.bitat.state.ReelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ReelViewModel : ViewModel() {
    val reelList = listOf("hello", "world")
    private val _state = MutableStateFlow((ReelState()))
    val state: StateFlow<ReelState> get() = _state.asStateFlow()


    fun setCurrentBlog(currentBlog: BlogBaseDto) {
        _state.update {
            it.copy(currentBlog = currentBlog)
        }
    }

    fun refreshCurrent(currentBlog: BlogBaseDto) {
        MainCo.launch {
            if (_state.value.resList.size > 0) {
                val resListIndex = _state.value.resList.indexOf(currentBlog)
                if (resListIndex >= 0) _state.update {
                    it.resList[resListIndex] = currentBlog
                    CuLog.debug(
                        CuTag.Blog,
                        "1111 data update success${it.resList[resListIndex].agrees}"
                    )
                    it
                }
            }
//            _state.update {
//                it.copy(currentBlog = currentBlog)
//            }
            _state.update {
                it.copy(flag = _state.value.flag + 1)
            }
        }
    }

    fun getList(isInit: Boolean) {
        MainCo.launch {

            _state.value.currentBlog?.let { item->

                if (isInit) {
                    _state.update {
                        it.resList.clear()
                       it
                    }

                    _state.update {
                        it.resList.add(item)
                        it
                    }

                }
                SearchReq.recommendSearchDetail(RecommendSearchDetailDto(blogId = item.id)).await()
                    .map { data ->
                        _state.update {
                            it.resList.addAll(data)
                            it
                        }
                    }.errMap {
                        CuLog.debug(
                            CuTag.Blog,
                            "recommendSearchDetail failed ，code：${it.code}  msg:${it.msg}"
                        )
                    }
            }
        }
    }

    fun setIndex(index: Int) {
        _state.update {
            it.copy(resIndex = index)
        }
    }
}