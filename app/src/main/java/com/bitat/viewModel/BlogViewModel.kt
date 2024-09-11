package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.INNER_TIMEOUT
import com.bitat.repository.dto.req.FollowBlogsDto
import com.bitat.repository.dto.req.NewBlogsDto
import com.bitat.repository.dto.req.TimeLineDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.store.UserStore
import com.bitat.state.BlogLoad
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogState
import com.bitat.ui.common.LoadMoreState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BlogViewModel : ViewModel() {
    val blogState = MutableStateFlow(BlogState())

    //    private val _eventFlow = MutableSharedFlow<String>()
    //    val eventFlow: SharedFlow<String> get() = _eventFlow
    fun firstFetchFinish() {
        blogState.update {
            it.copy(isFirst = false)
        }
    }

    fun initBlogList(
        menu: BlogMenuOptions = BlogMenuOptions.Recommend,
        isRefresh: Boolean = false
    ) { // TODO
        if (blogState.value.updating) {
            return
        }
        MainCo.launch {
            blogState.update {
                it.copy(updating = true)
            }

            blogState.update {
                it.blogList.clear()
                it
            }
            when (blogState.value.currentMenu) {
                BlogMenuOptions.Recommend -> {
                    CuLog.debug(CuTag.Blog, "调用Recommend")
                    BlogReq.recommendBlogs().await().map { data ->
                        blogState.update {
                            it.blogList.addAll(data)
                            it
                        }
                        blogState.update {
                            it.copy(updating = false)
                        }
                    }.errMap {
                        blogState.update {
                            it.copy(updating = false)
                        }
                        CuLog.error(
                            CuTag.Blog,
                            "recommendBlogs----errMap: code=${it.code},msg=${it.msg}"
                        )
                    }
                }

                BlogMenuOptions.Latest -> {
                    CuLog.debug(CuTag.Blog, "调用Latest")
                    BlogReq.newBlogs(NewBlogsDto(20)).await().map { data ->
                        blogState.update {
                            it.blogList.addAll(data)
                            it
                        }
                        blogState.update {
                            it.copy(updating = false)
                        }

                        CuLog.debug(CuTag.Blog, "Latest 返回数据：${data.size}")
                    }.errMap {
                        blogState.update {
                            it.copy(updating = false)
                        }
                        CuLog.error(CuTag.Blog, "Latest----errMap: code=${it.code},msg=${it.msg}")
                    }
                }

                BlogMenuOptions.Followed -> {
                    CuLog.debug(CuTag.Blog, "调用Followed")
                    BlogReq.followBlogs(FollowBlogsDto(20)).await().map { data ->
                        CuLog.debug(CuTag.Blog, "调用Followed,返回数据：${data.size}")
                        if (data.isNotEmpty()) {
                            blogState.update {
                                it.blogList.addAll(data)
                                it
                            }
                        }
                        blogState.update {
                            it.copy(updating = false)
                        }
                    }.errMap {
                        blogState.update {
                            it.copy(updating = false)
                        }
                        CuLog.error(
                            CuTag.Blog,
                            "调用Followed----errMap: code=${it.code},msg=${it.msg}"
                        )
                    }
                }
            }
        }
    }


    fun setCurrentBlog(currentBlog: BlogBaseDto) {
        blogState.update {
            it.copy(currentBlog = currentBlog)
        }

    }

    fun refreshCurrent(currentBlog: BlogBaseDto) {
        MainCo.launch {
            val blogListIndex = blogState.value.blogList.indexOf(currentBlog)

            if (blogListIndex > 0) blogState.update {
                it.blogList[blogListIndex] = currentBlog
                it
            }

            blogState.update {
                it.copy(flag = blogState.value.flag + 1)
            }
        }
    }

    fun switchBlogMenu(menu: BlogMenuOptions) {
        blogState.update {
            it.copy(currentMenu = menu)
        }

        initBlogList(blogState.value.currentMenu, isRefresh = true)
    }

    fun topBarState(isShow: Boolean) {
        blogState.update {
            it.copy(topBarShow = isShow)
        }
    }

    fun loadMore(successFn: () -> Unit) {
        if (blogState.value.updating) {
            return
        }
        loadState(BlogLoad.Default)

        MainCo.launch {
            blogState.update {
                it.copy(updating = true)
            }
            isLoadMore(true)
            BlogReq.recommendBlogs().await().map { data ->
                blogState.update {
                    it.blogList.addAll(data)
                    it
                }
                blogState.update {
                    it.copy(updating = false)
                }

                if (data.isNotEmpty()) {
                    isLoadMore(false)
                    loadState(BlogLoad.Success)
                } else {
                    loadState(BlogLoad.NoData)
                }
            }.errMap {
                CuLog.debug(CuTag.Blog, "recommendBlogs----errMap: code=${it.code},msg=${it.msg}")
                blogState.update { tt ->
                    tt.copy(updating = false)
                }
                when (it.code) {
                    INNER_TIMEOUT -> loadState(BlogLoad.TimeOut)
                    else -> loadState(BlogLoad.Fail)
                }

            }
        }
    }

    fun collectClick(blog: BlogBaseDto) {
        blog.hasCollect = !blog.hasCollect
        blog.collects = if (blog.hasCollect) blog.collects + 1u else blog.collects - 1u
        refreshCurrent(blog)
    }

    fun likeClick(blog: BlogBaseDto) {
        blog.hasPraise = !blog.hasPraise
        blog.agrees = if (blog.hasPraise) blog.agrees + 1u else blog.agrees - 1u
        refreshCurrent(blog)
    }

    fun commentClick(blog: BlogBaseDto) {
        blog.comments += 1u
        refreshCurrent(blog)
    }

    fun isLoadMore(loadMore: Boolean) {
        blogState.update {
            it.copy(isLoadMore = loadMore)
        }
    }

    fun loadState(state: BlogLoad) {
        blogState.update {
            it.copy(loadResp = state)
        }
    }

    fun listCurrent(index: Int) {
        blogState.update {
            it.copy(currentListIndex = index)
        }
    }

    fun listOffSet(offset: Int) {
        blogState.update {
            it.copy(listOffset = offset)
        }
    }

    fun removeOne(blog: BlogBaseDto) {
        val index = blogState.value.blogList.indexOf(blog)
        if (index >= 0) {
            blogState.update {
                it.blogList.removeAt(index)
                it
            }
            blogState.update {
                it.copy(flag = it.flag + 1)
            }
        }

    }


}