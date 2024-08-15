package com.bitat.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.repository.http.service.BlogReq
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/7/29  17:18
 *    desc   :
 */
class HomeViewModel() : ViewModel() {
    val blogState = MutableStateFlow(BlogState())

//    private val _eventFlow = MutableSharedFlow<String>()
//    val eventFlow: SharedFlow<String> get() = _eventFlow

    fun initBlogList(type: BlogMenuOptions = BlogMenuOptions.Recommend) {
        // TODO
        if (blogState.value.updating) {
            return
        }
        MainCo.launch {
            blogState.update {
                it.copy(updating = true)
            }
            BlogReq.recommendBlogs().await().map { data ->
//              val mutableData=  data.map {
//                    mutableStateOf(it)
//                }
                blogState.update {
                    it.blogList.addAll(data)
                    it
                }
                blogState.update {
                    it.copy(updating = false)
                }
            }
        }
    }

    fun updateInfo(index: Int) {
        val blog = blogState.value.blogList[index]
        blog.content = "askdfjasdf"

        blogState.update {
            it.blogList[index] = blog
            it
        }
    }

    fun setCurrentBlog(currentBlog: BlogBaseDto) {
        blogState.update {
            it.copy(currentBlog = currentBlog)
        }
    }


//    fun test(){
//
//
//        MsgDto.parseFrom()
//    }

}
