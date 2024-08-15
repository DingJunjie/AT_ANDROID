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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.ui.component.Avatar
import com.bitat.ui.component.UserInfo
import com.bitat.ui.reel.CuExoPlayer
import com.bitat.ui.theme.Typography
import com.bitat.viewModel.BlogViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun BlogDetailPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val blogState = vm.blogState.collectAsState()
    val blogDetail = blogState.value.currentBlog;

    Log.i("BlogDetail", "current blog is $blogDetail")

    Scaffold(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 20.dp), topBar = {
        TopBar() {
            navHostController.popBackStack()
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            UserInfo(username = blogDetail?.nickname ?: "")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text("content")
            }

            //            LazyColumn(modifier = Modifier.fillMaxSize()) {
            //                item(content = videoList.size) { item ->
            //
            //                }
            //
            //            }


        }
    }

}

@Composable
fun TopBar(backFn: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 20.dp)) {
        IconButton(onClick = { /*TODO*/ }) {
            SvgIcon(
                path = "svg/arrow-left.svg",
                contentDescription = "",
                modifier = Modifier.size(20.dp)
            )
        }
        Text("帖子")
    }
}


