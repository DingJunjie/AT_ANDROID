package com.bitat.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ext.clickableWithoutRipple
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo
import com.bitat.repository.store.UserStore
import com.bitat.ui.component.BackButton
import com.bitat.utils.QiNiuUtil
import com.bitat.viewModel.ChatDetailsViewModel
import com.bitat.viewModel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatHistoryPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ChatDetailsViewModel::class]
    val state by vm.state.collectAsState()

    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()

    val options = listOf("消息", "分享")
    val pagerState = rememberPagerState {
        options.size
    }

    val keyword = remember {
        mutableStateOf("")
    }

    Scaffold() { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ChatHistoryTopBar(pagerState, options) {
                navHostController.popBackStack()
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xffeeeeee)),
                verticalAlignment = Alignment.Top
            ) { index ->
                when (index) {
                    0 -> ChatHistoryMessage(
                        keyword.value,
                        {
                            keyword.value = it
                        },
                        chatState.currentRoom,
                        state.messageList
                    )
                }
            }
        }

    }
}

@Composable
fun ChatHistoryMessage(
    searchKeyword: String,
    updateKeyword: (String) -> Unit,
    room: SingleRoomPo,
    messageList: List<SingleMsgPo>
) {
    Column {
        ChatHistorySearchField(searchKeyword, updateKeyword)
        messageList.map {
            MessageItem(room = room, msg = it)
        }
    }
}

@Composable
fun ChatHistorySearchField(searchKeyword: String, updateKeyword: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 10.dp)
    ) {
        BasicTextField(value = searchKeyword,
            singleLine = true,
            onValueChange = {
                updateKeyword(it)
            },
            keyboardActions = KeyboardActions(onDone = {

            }) { },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun MessageItem(room: SingleRoomPo, msg: SingleMsgPo) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Avatar(url = QiNiuUtil.QINIU_PUB_PREFIX + room.profile)
                Column(modifier = Modifier.weight(1f)) {
                    Text(room.nickname)
                    Text(msg.content)
                }
                Column {
                    Text(msg.time.toString())
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatHistoryTopBar(pagerState: PagerState, options: List<String>, backFn: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton {
                backFn()
            }
            Text("查找聊天记录")
        }


        TabRow(selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .width(10.dp), height = 2.5.dp, color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), divider = {}) {
            options.forEachIndexed { index, item ->
                val selected = index == pagerState.currentPage
                Text(text = item, color = if (selected) {
                    Color.Black
                } else {
                    Color.Gray
                }, fontSize = 17.sp, modifier = Modifier
                    .clickableWithoutRipple {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
//                            onSelect(index)
                        }
                    }
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), textAlign = TextAlign.Center
                )
            }
        }
    }
}