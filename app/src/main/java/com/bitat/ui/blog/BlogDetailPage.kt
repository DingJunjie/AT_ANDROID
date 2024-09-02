package com.bitat.ui.blog

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.Density
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.router.AtNavigation
import com.bitat.ui.common.FollowBtn
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.CommentPopup
import com.bitat.ui.component.UserInfoWithAddr
import com.bitat.ui.theme.Typography
import com.bitat.utils.RelationUtils
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CollectViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun BlogDetailPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val blogState = vm.blogState.collectAsState()

    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    val collectVm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val collectState by collectVm.collectState.collectAsState()

    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]

    val coroutineScope = rememberCoroutineScope()

    val blogDetail = blogState.value.currentBlog
    val heigh = getHeight(blogState.value.currentBlog!!)
    val scrollState = rememberScrollState()
    var isCommentVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Dispatchers.IO) {
        blogDetail?.let {
            commentVm.updateBlogId(it.id)
            delay(1000)
            isCommentVisible = true
        }

    }

    BackHandler {
        CuLog.debug(CuTag.Blog, "点击系统返回")
        AtNavigation(navHostController).navigateToHome()
    }

    Scaffold(modifier = Modifier.fillMaxWidth().fillMaxHeight(), topBar = {
        TopBar(title = stringResource(id = R.string.blog_post)) {
            AtNavigation(navHostController).navigateToHome()
        }
    }) { padding ->
        Column(modifier = Modifier.verticalScroll(scrollState).padding(padding) ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            blogState.value.currentBlog?.let {
                Row(modifier = Modifier.height(80.dp).padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    Box(modifier = Modifier.weight(3f)) {
                        UserInfoWithAddr(nickname = it.nickname,
                            createTime = it.createTime,
                            isShowTime = true,
                            avatar = it.profile,
                            address = it.ipTerritory)
                    }

                    FollowBtn(modifier = Modifier.weight(1f).height(40.dp),
                        rel = it.rel,
                        it.userId,
                        clickFn = { rel ->
                            it.rel=rel
                           vm.refreshCurrent(it)
                        } )
                }
                Column(modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp)) {
                    Text(text = it.content,
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = TextStyle(lineHeight = MaterialTheme.typography.bodyMedium.lineHeight))
                    BlogContent(it.kind.toInt(),
                        it,
                        heigh,
                        needRoundedCorner = true,
                        isPlaying = true,
                        coverIsFull = true,
                        navHostController,
                        viewModelProvider)
                    Spacer(modifier = Modifier.height(40.cdp))
                    BlogOperation(it, tapComment = {}, tapAt = {}, tapCollect = { index ->
                        vm.collectClick(it)
                    }, tapLike = {
                        vm.likeClick(it)
                    })
                    Spacer(modifier = Modifier.height(60.cdp))
                }
                Column(modifier = Modifier.height(53.cdp)
                    .background(MaterialTheme.colorScheme.primary).padding(start = 5.dp, end = 5.dp)
                    .clip(CircleShape),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "全部评论（${it.comments}）",
                        style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimary))
                }
                Spacer(modifier = Modifier.height(30.cdp))
                if (isCommentVisible) {
                    CommentPopup(visible = true,
                        blogId = commentState.currentBlogId,
                        commentViewModel = commentVm,
                        coroutineScope = coroutineScope,
                        tapImage = {
                            imagePreviewVm.setImagePreView(arrayOf(it))
                            AtNavigation(navHostController).navigateToImagePreviewPage()
                        },
                        commentState = commentState,
                        onClose = { },
                        isPop = false) {
                        it.comments += 1u
                        vm.refreshCurrent(it)
                    }
                    if (blogState.value.flag < 0) {
                        Text(text = "")
                    }
                }
            }
        }
    }


}

@Composable
fun TopBar(title: String, backFn: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Spacer(modifier = Modifier.statusBarsPadding().fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp)) {

            IconButton(onClick = {
                CuLog.debug(CuTag.Blog, "")
                backFn()
            }) {
                SvgIcon(path = "svg/arrow-left.svg",
                    contentDescription = "",
                    modifier = Modifier.size(20.dp))
            }
            Text(text = title)
        }
    }
}


