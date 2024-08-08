package com.bitat.ui.blog

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.router.AtNavigation
import com.bitat.style.FontStyle
import com.bitat.ui.common.CarmeraOpen
import com.bitat.ui.theme.white
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import com.bitat.viewModel.BlogViewModel
import kotlinx.coroutines.Dispatchers.IO

/***
 * 首页的数据显示
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BlogPage(modifier: Modifier,
    navController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val blogState by vm.blogState.collectAsState()
    //获取 状态栏高度 用于设置上边距
    var paddingStatusBar by remember { mutableStateOf(statusBarHeight) }

    LaunchedEffect(IO) {
        vm.initBlogList()
    }

    Log.i("BlogPage", "========>>>>$statusBarHeight")
    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(white)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            BlogTopBar()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                LazyColumn(modifier = Modifier.fillMaxSize()) { // println("blogState.blogList>>>>>>>>>>>>>>"+vm.blogs.size)
                    items(blogState.blogList) { item -> //Text(item.content)
                        Surface(
                            modifier = Modifier
                                .clickable(onClick = {
                                    vm.setCurrentBlog(item)
                                    AtNavigation(navController).navigateToBlogDetail()
                                })
                                .fillMaxWidth()
                        ) {
                            BlogItem(item)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun BlogTopBar() {
    Row(
        modifier = Modifier
            .height(30.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "推荐",
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color.Black,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(
                start = 10.dp,
                top = 5.dp,
                end = 5.dp,
                bottom = 5.dp
            ),
            fontSize = FontStyle.contentLargeSize
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End
        ) {
            SvgIcon(path = "svg/search.svg", tint = Color.Black, contentDescription = "")
        }
    }
}




