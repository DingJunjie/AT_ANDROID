package com.bitat.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
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
                    BlogReq.newBlogs(NewBlogsDto(20)).await().map { data ->
                        blogState.update {
                            it.blogList.addAll(data)
                            it
                        }
                        blogState.update {
                            it.copy(updating = false)
                        }
                    }.errMap {
                        CuLog.debug(CuTag.Blog,
                            "Blogs----errMap: code=${it.code},msg=${it.msg}")
                    }
                }
                BlogMenuOptions.Followed -> {
                    BlogReq.followBlogs(FollowBlogsDto(20)).await().map { data ->
                        blogState.update {
                            it.blogList.addAll(data)
                            it
                        }
                        blogState.update {
                            it.copy(updating = false)
                        }
                    }.errMap {
                        CuLog.debug(CuTag.Blog,
                            "Blogs----errMap: code=${it.code},msg=${it.msg}")

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

    fun pageTypeChange(type: BlogMenuOptions) {
        blogState.update {
            it.copy(currentMenu = type)
        }
    }
}