package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.FollowBlogsDto
import com.bitat.repository.dto.req.NewBlogsDto
import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.http.service.SocialReq
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BlogViewModel : ViewModel() {
    val blogState = MutableStateFlow(BlogState())

    //    private val _eventFlow = MutableSharedFlow<String>()
    //    val eventFlow: SharedFlow<String> get() = _eventFlow

    fun initBlogList(menu: BlogMenuOptions = BlogMenuOptions.Recommend) { // TODO
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

    fun switchBlogMenu(menu: BlogMenuOptions) {
        blogState.update {
            it.copy(currentMenu = menu)
        }
    }

    fun topBarState(isShow: Boolean) {
        blogState.update {
            it.copy(topBarShow = isShow)
        }
    }

    fun getCommentList() {
        MainCo.launch {
            blogState.update {
                it.copy(updating = true)
            }

            blogState.update {
                it.blogList.clear()
                it
            }
            BlogReq.recommendBlogs()
        }
    }

    fun loadMore() {
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


}