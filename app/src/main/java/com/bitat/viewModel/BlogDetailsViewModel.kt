package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.state.BlogDetailsState
import com.bitat.state.BlogDetailsType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 *    author : shilu
 *    date   : 2024/9/4  10:31
 *    desc   : 博文详情页
 */
class BlogDetailsViewModel : ViewModel() {
    val _state = MutableStateFlow(BlogDetailsState())
    val state = _state.asStateFlow()

    fun collectClick(blog: BlogBaseDto) {
        blog.hasCollect = !blog.hasCollect
        blog.collects = if (blog.hasCollect) blog.collects + 1u else blog.collects - 1u
        setCurrentBlog(blog)
        updateFlag()
    }

    fun setCurrentBlog(blog: BlogBaseDto) {
        _state.update {
            it.copy(currentBlog = blog)
        }
        updateFlag()
    }

    fun pageType(type: BlogDetailsType) {
        _state.update {
            it.copy(detailsType = type)
        }
    }

    fun updateFlag() {
        _state.update {
            it.copy(flag = state.value.flag + 1)
        }
    }

    fun likeClick(blog: BlogBaseDto) {
        blog.hasPraise = !blog.hasPraise
        blog.agrees = if (blog.hasPraise) blog.agrees + 1u else blog.agrees - 1u
        setCurrentBlog(blog)
        updateFlag()

    }

}