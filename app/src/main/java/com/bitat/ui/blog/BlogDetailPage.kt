package com.bitat.ui.blog

import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.ext.cdp
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
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.BlogImages
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.BlogVideo
import com.bitat.ui.component.UserInfo
import com.bitat.ui.reel.CuExoPlayer
import com.bitat.ui.theme.Typography
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun BlogDetailPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val blogState = vm.blogState.collectAsState()
    val blogDetail = blogState.value.currentBlog
    val heigh = getHeight(blogState.value.currentBlog!!)
    val scrollState = rememberScrollState()

    Log.i("BlogDetail", "current blog is $blogDetail")

    Scaffold(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(top = 20.dp), topBar = {
        TopBar {
            navHostController.popBackStack()
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding)//                .verticalScroll(scrollState)
        ) {
            blogState.value.currentBlog?.let {
                Row(modifier = Modifier.height(80.dp).padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    CircleImage(it.profile, modifier = Modifier.size(140.cdp))
                    UserInfo(username = it.nickname, it.createTime, true)
                    Button(modifier = Modifier.size(80.dp, 50.dp).clip(CircleShape),
                        onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.blog_dtl_followed))
                    }
                }
                Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text(text = it.content,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                    when (it.kind.toInt()) {
                        BLOG_TEXT_ONLY -> {}
                        BLOG_VIDEO_ONLY, BLOG_VIDEO_TEXT -> BlogVideo(
                            it,
                            heigh,
                            true,
                            navHostController
                        )

                        BLOG_AUDIO_ONLY -> BlogAudioOnlyShow(it)
                        BLOG_IMAGE_TEXT, BLOG_IMAGES_ONLY -> BlogImages(it,
                            heigh)
                    //                        BLOG_AUDIO_TEXT -> BlogAudioTextShow(mBlogBaseDto)
                        //                        BLOG_VIDEO_IMAGE -> BlogVideoImageShow(mBlogBaseDto)
                        //                        BLOG_AUDIO_IMAGE -> BlogAudioImageShow(mBlogBaseDto)
                        //                        BLOG_AUDIO_IMAGE_TEXT -> BlogAudioImageTextShow(mBlogBaseDto)
                        //                        BLOG_VIDEO_IMAGE_TEXT -> BlogVideoImageTextShow(mBlogBaseDto)
                        //                        BLOG_RICH_TEXT -> BlogRichTextShow(mBlogBaseDto)
                        //                        PODCASTS -> PodcastsShow(mBlogBaseDto)
                        //                        RUSTIC -> RusticShow(mBlogBaseDto)
                        //                        VIRTUAL -> VirtualShow(mBlogBaseDto)
                        //                        POETRY -> PoetryShow(mBlogBaseDto)
                        //                        ESSAY -> EssayShow(mBlogBaseDto)
                        //                        NOVEL -> NovelShow(mBlogBaseDto)
                    }
                    Surface(modifier = Modifier.padding(start = ScreenUtils.screenWidth.times(0.12).dp)) {
                        BlogOperation(it)
                    }
                    LazyColumn {

                    }
                }
            }
        }
    }

}

@Composable
fun TopBar(backFn: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 20.dp)) {
        IconButton(onClick = {
            CuLog.debug(CuTag.Blog, "")
            backFn()
        }) {
            SvgIcon(path = "svg/arrow-left.svg",
                contentDescription = "",
                modifier = Modifier.size(20.dp))
        }
        Text(text = stringResource(id = R.string.blog_post))
    }
}


