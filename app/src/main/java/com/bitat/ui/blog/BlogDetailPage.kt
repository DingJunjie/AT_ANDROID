package com.bitat.ui.blog

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.collectAsState
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
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.FollowBtn
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.component.BlogOperation
import com.bitat.ui.component.UserInfoWithAddr
import com.bitat.utils.RelationUtils
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

    Scaffold(modifier = Modifier.fillMaxWidth().fillMaxHeight(), topBar = {
        TopBar {
            navHostController.popBackStack()
        }
    }) { padding ->
        Column(modifier = Modifier.verticalScroll(scrollState).padding(padding) // 使Column支持垂直滚动
        ) {
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
                        it.userId.toLong())
                }
                Column(modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp)) {
                    Text(text = it.content,
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = TextStyle(lineHeight = MaterialTheme.typography.bodyMedium.lineHeight))
                    BlogContent(it.kind.toInt(),
                        it,
                        heigh,
                        true,
                        navHostController,
                        viewModelProvider)
                    BlogOperation(it)
                    Spacer(modifier = Modifier.height(30.dp))

                }
                Column(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Gray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${blogDetail?.comments}个评论")
                }
            }
        }
    }

}

@Composable
fun TopBar(backFn: () -> Unit) {
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
            Text(text = stringResource(id = R.string.blog_post))
        }
    }

}


