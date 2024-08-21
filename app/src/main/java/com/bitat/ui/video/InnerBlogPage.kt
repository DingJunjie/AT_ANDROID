package com.bitat.ui.video

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.viewModel.InnerBlogViewModel

/**
 *    author : shilu
 *    date   : 2024/8/13  17:42
 *    desc   :  视频/图片 播放页面
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InnerBlogPage(viewModelProvider: ViewModelProvider) {
    val viewModel: InnerBlogViewModel = viewModel()
    val state by viewModel.innerState.collectAsState()

    val pagerState = rememberPagerState(pageCount = { state.blogList.size })

    //监听页面状态的变化
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> // 页面切换时触发的操作
            CuLog.debug(CuTag.Blog, "切换页面，Current page: $page")

        }
    }

    VerticalPager(state = pagerState) { page -> // Our page content
        if (state.blogList[page].kind.toInt() == BLOG_VIDEO_ONLY || state.blogList.get(page).kind.toInt() == BLOG_VIDEO_TEXT) {

            //视频类型
//            CuExoPlayer(
//                data = videos[page],
//                modifier = Modifier.fillMaxSize(),
//                cover = state.blogList[page].cover,
//                isFixHeight = true
//            )
        } else {

            // 图片类型
        }

    }


}