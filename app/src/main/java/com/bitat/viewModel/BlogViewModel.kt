package com.bitat.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.repository.http.service.BlogReq
import com.bitat.state.BlogState
import com.bitat.state.BlogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BlogViewModel : ViewModel() {
    val blogState = MutableStateFlow(BlogState())

    //    private val _eventFlow = MutableSharedFlow<String>()
    //    val eventFlow: SharedFlow<String> get() = _eventFlow

    fun initBlogList(type: BlogType = BlogType.Recommend) { // TODO
        if (blogState.value.updating) {
            return
        }
        MainCo.launch {
            blogState.update {
                it.copy(updating = true)
            }
            try {
                BlogReq.recommendBlogs().await().map { data ->
//                    if (data.isEmpty()) { // 如果第一条为视频类型，设置默认播放
//                        val firstBolg = data[0]
//                        if (firstBolg.kind.toInt() == BLOG_VIDEO_ONLY || firstBolg.kind.toInt() == BLOG_VIDEO_TEXT) {
//                            firstBolg.isCurrent = true
//                        }
//                    }


                    blogState.update {
                        it.blogList.addAll(data)
                        it
                    }
                    blogState.update {
                        it.copy(updating = false)
                    }
                }
            } catch (e: Exception) {

            }

        }
    }

    fun setCurrentBlog(currentBlog: BlogBaseDto) {
        blogState.update {
            it.copy(currentBlog = currentBlog)
        }
    }


}