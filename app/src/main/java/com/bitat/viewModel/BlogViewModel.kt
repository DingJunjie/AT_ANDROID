package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_AUDIO_IMAGE
import com.bitat.repository.consts.BLOG_AUDIO_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_AUDIO_ONLY
import com.bitat.repository.consts.BLOG_AUDIO_TEXT
import com.bitat.repository.consts.BLOG_IMAGES_ONLY
import com.bitat.repository.consts.BLOG_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_RICH_TEXT
import com.bitat.repository.consts.BLOG_TEXT_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_IMAGE
import com.bitat.repository.consts.BLOG_VIDEO_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.repository.consts.ESSAY
import com.bitat.repository.consts.NOVEL
import com.bitat.repository.consts.PODCASTS
import com.bitat.repository.consts.POETRY
import com.bitat.repository.consts.RUSTIC
import com.bitat.repository.consts.VIRTUAL
import com.bitat.repository.dto.req.FollowBlogsDto
import com.bitat.repository.dto.req.NewBlogsDto
import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.http.service.SocialReq
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogState
import com.bitat.ui.blog.BlogAudioImageShow
import com.bitat.ui.blog.BlogAudioImageTextShow
import com.bitat.ui.blog.BlogAudioOnlyShow
import com.bitat.ui.blog.BlogAudioTextShow
import com.bitat.ui.blog.BlogRichTextShow
import com.bitat.ui.blog.BlogTextOnlyShow
import com.bitat.ui.blog.BlogVideoImageShow
import com.bitat.ui.blog.BlogVideoImageTextShow
import com.bitat.ui.blog.EssayShow
import com.bitat.ui.blog.NovelShow
import com.bitat.ui.blog.PodcastsShow
import com.bitat.ui.blog.PoetryShow
import com.bitat.ui.blog.RusticShow
import com.bitat.ui.blog.VirtualShow
import com.bitat.ui.component.BlogImages
import com.bitat.ui.component.BlogVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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
                        CuLog.debug(
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

    fun filterResList() {
        val filterList =
            blogState.value.blogList.filter { it.kind.toInt() == BLOG_VIDEO_ONLY || it.kind.toInt() == BLOG_VIDEO_TEXT || it.kind.toInt() == BLOG_IMAGE_TEXT || it.kind.toInt() == BLOG_IMAGES_ONLY }
        if (filterList.isNotEmpty())
            blogState.update {
                it.resList.addAll(filterList)
                it
            }
    }


    fun setCurrentBlog(currentBlog: BlogBaseDto) {
        when (currentBlog.kind.toInt()) {
            BLOG_VIDEO_ONLY, BLOG_VIDEO_TEXT, BLOG_IMAGE_TEXT, BLOG_IMAGES_ONLY -> {
                val resIndex = blogState.value.resList.indexOf(currentBlog)
                if (resIndex > 0)
                    blogState.update { it.copy(resIndex = resIndex) }
            }
        }

        blogState.update {
            it.copy(currentBlog = currentBlog)
        }
    }

    fun setResIndex(index: Int) {
        blogState.update { it.copy(resIndex = index) }
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