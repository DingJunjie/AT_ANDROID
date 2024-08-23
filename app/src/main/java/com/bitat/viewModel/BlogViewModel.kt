package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.FollowBlogsDto
import com.bitat.repository.dto.req.NewBlogsDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogState
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

    fun initBlogList(menu: BlogMenuOptions = BlogMenuOptions.Recommend, isRefresh: Boolean = false) { // TODO
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
                        CuLog.debug(CuTag.Blog,
                            "recommendBlogs----errMap: code=${it.code},msg=${it.msg}")
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
                    }.errMap {
                        CuLog.debug(CuTag.Blog, "Blogs----errMap: code=${it.code},msg=${it.msg}")
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
                        CuLog.debug(CuTag.Blog, "Blogs----errMap: code=${it.code},msg=${it.msg}")

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
                CuLog.debug(CuTag.Blog,
                    "111 blog列表更新成功: ${it.blogList[blogListIndex].hasPraise}")
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
        CuLog.debug(CuTag.Blog, "加载数据，${blogState.value.currentMenu}")
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
        MainCo.launch {
            blogState.update {
                it.copy(updating = true)
            }
            BlogReq.recommendBlogs().await().map { data ->
                blogState.update {
                    it.blogList.addAll(data)
                    it
                }
                blogState.update {
                    it.copy(updating = false)
                }
            }.errMap {
                CuLog.debug(CuTag.Blog, "recommendBlogs----errMap: code=${it.code},msg=${it.msg}")
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
        blog.comments =  blog.comments + 1u
        refreshCurrent(blog)
    }
}