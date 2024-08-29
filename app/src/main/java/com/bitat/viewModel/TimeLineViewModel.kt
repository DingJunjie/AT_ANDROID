package com.bitat.viewModel


import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.HTTP_PAGESIZE
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.req.TimeLineDto
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.state.TimeLineState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/8/27  18:13
 *    desc   : 时间线
 */
class TimeLineViewModel : ViewModel() {

    val state = MutableStateFlow(TimeLineState())


    fun timeLineInit(userId: Long, lastTime: Long = 0L) {

        if (state.value.updating) {
            return
        }

        MainCo.launch {

            state.update {
                it.copy(updating = true)
            }
            isLoadMore(true)
            loadState(HttpLoadState.Default)
            BlogReq.timeLine(TimeLineDto(HTTP_PAGESIZE, userId,lastTime)).await().map { data ->
                state.update {
                    it.copy(updating = false)
                }
                if (lastTime == 0L) state.update {
                    it.timeLineList.clear()
                    it
                }
                if (data.isNotEmpty()) {
                    state.update {
                        it.timeLineList.addAll(data)
                        it
                    }
                    loadState(HttpLoadState.Success)
                    isLoadMore(false)
                } else {
                    loadState(HttpLoadState.NoData)
                }
            }.errMap {
                state.update {
                    it.copy(updating = false)
                }
                loadState(HttpLoadState.Fail)
                CuLog.error(CuTag.Profile, "timeLine fail code:${it.code},msg:${it.msg}")
            }
        }
    }

    fun clearTimeLine() {
        state.update {
            it.timeLineList.clear()
            it
        }
    }

    fun firstFetchFinish() {
        state.update {
            it.copy(isFirst = false)
        }
    }

    fun likeClick(blog: BlogBaseDto) {
        blog.hasPraise = !blog.hasPraise
        blog.agrees = if (blog.hasPraise) blog.agrees + 1u else blog.agrees - 1u
        refreshCurrent(blog)
    }

    private fun refreshCurrent(currentBlog: BlogBaseDto) {
        MainCo.launch {
            val blogListIndex = state.value.timeLineList.indexOf(currentBlog)

            if (blogListIndex > 0) state.update {
                it.timeLineList[blogListIndex] = currentBlog
                it
            }

            state.update {
                it.copy(flag = state.value.flag + 1)
            }

        }
    }

    fun collectClick(blog: BlogBaseDto) {
        blog.hasCollect = !blog.hasCollect
        blog.collects = if (blog.hasCollect) blog.collects + 1u else blog.collects - 1u
        refreshCurrent(blog)
    }


    fun commentClick(blog: BlogBaseDto) {
        blog.comments += 1u
        refreshCurrent(blog)
    }

    fun setCurrentBlog(currentBlog: BlogBaseDto) {
        state.update {
            it.copy(currentBlog = currentBlog)
        }

    }

    fun isLoadMore(loadMore: Boolean) {
        state.update {
            it.copy(isLoadMore = loadMore)
        }
    }

    fun loadState(loadState: HttpLoadState) {
        state.update {
            it.copy(loadResp = loadState)
        }
    }


    fun reset() {
        state.update {
            it.copy(isFirst = true,
                isLoadMore = false,
                flag = 0,
                updating = false,
                loadResp = HttpLoadState.Default,
                currentBlog = null)
        }

        state.update {
            it.timeLineList.clear()
            it
        }
    }
}