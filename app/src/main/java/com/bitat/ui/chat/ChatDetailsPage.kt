package com.bitat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ext.Density
import com.bitat.ext.clickableWithoutRipple
import com.bitat.repository.consts.CHAT_Text
import com.bitat.repository.po.SingleMsgPo
import com.bitat.ui.common.CarmeraOpen
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.BackButton
import com.bitat.ui.component.EmojiTable
import com.bitat.ui.discovery.CardView
import com.bitat.ui.theme.Typography
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ChatDetailsViewModel
import com.bitat.viewModel.ChatViewModel
import kotlinx.coroutines.Dispatchers.IO

/**
 *    author : shilu
 *    date   : 2024/8/6  10:49
 *    desc   : 聊天页
 */

@Composable
fun ChatDetailsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {

    val vm: ChatDetailsViewModel = viewModel()
    val state by vm.state.collectAsState()

    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()

    val chatInput = remember { mutableStateOf("") }
    val showMsgOpt = remember {
        mutableStateOf(false)
    }

    val currentPointerOffset = remember {
        mutableStateOf(Offset(0f, 0f))
    }

    LaunchedEffect(IO) {
        vm.getMessage(chatState.currentRoom!!.otherId)
    }

    val currentItemCoor = remember {
        mutableStateOf(0)
    }

    val canTouch = remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        ChatDetailTopBar(name = chatState.currentUserInfo?.nickname ?: "",
            avatar = chatState.currentUserInfo?.profile ?: "",
            backButton = {
                chatVm.clearRoom()
                navHostController.popBackStack()
            },
            goProfile = { /*TODO*/ }) {}
    }, bottomBar = {
        ChatBottomContainer(message = chatInput.value, {
            chatInput.value = it
        }, {
            vm.sendMessage(chatState.currentRoom!!.otherId, 1, it)
        })
    }) { padding ->

        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        currentPointerOffset.value = it
                        showMsgOpt.value = true
                    })
                }
        ) {
            items(state.messageList) { item ->


//                TimeMessage(timestamp = 1231891839)
//                Box(modifier = Modifier.pointerInput(Unit) {
//                    detectTapGestures(onTap = {
//                        currentPointerOffset.value = it
//                        showMsgOpt.value = true
//                    })
//                }) {
////                    MessageList()
//                }
//                RecallMessage(nickname = "hello")
//                SenderReplyMessage("haha")
//                SenderImage("https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg")
//                Box(modifier = Modifier.pointerInput(Unit) {
//                    detectTapGestures(onTap = {
//                        currentPointerOffset.value = it
//                        showMsgOpt.value = true
//                    })
//                }) {
////                    MessageList()
//                }
                Box(modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
//                            canTouch.value = true
                        })
                    }
                ) {
                    ChatSenderMessage(item)
                }
            }
        }

//        ChatDetailTopBar(
//            name = "hello",
//            avatar = "",
//            backButton = { /*TODO*/ },
//            goProfile = { /*TODO*/ }) {
//        }
    }

    if (showMsgOpt.value)
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .clickableWithoutRipple {
                showMsgOpt.value = false
                canTouch.value = false
            }) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.offset(
                    currentPointerOffset.value.x.div(Density).toInt().dp,
                    currentPointerOffset.value.y.plus(currentItemCoor.value).dp
//                .div(Density).toInt().dp
                )
            ) {
                ChatMessageOpt()
            }
        }
}

//@Composable
//fun MessageList() {
//    SenderMessage(message = "hello world")
//}

@Composable
fun ChatBottomContainer(
    message: String,
    msgChange: (String) -> Unit,
    sendMessage: (String) -> Unit
) {
    val optShow = remember {
        mutableStateOf(false)
    }

    val emojiShow = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        ) {
            Text("for reply")
        }
        ChatInputField(message, msgChange, toggleEmoji = {
            optShow.value = false
            emojiShow.value = !emojiShow.value
        }, toggleOperation = {
            emojiShow.value = false
            optShow.value = !optShow.value
        }, sendMessage)
        if (emojiShow.value) EmojiTable(onTextAdded = {
            msgChange(message + it)
        })
        if (optShow.value) ChatOperations()
    }
}

@Composable
fun ChatSenderMessage(msg: SingleMsgPo) {
    when (msg.kind) {
        CHAT_Text ->
            SenderMessage(msg)
    }
}

@Composable
fun ChatInputField(
    message: String,
    msgChange: (String) -> Unit,
    toggleEmoji: () -> Unit,
    toggleOperation: () -> Unit,
    sendMessage: (String) -> Unit
) {
    val isText = remember {
        mutableStateOf(true)
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { isText.value = !isText.value }) {
            Icon(
                if (isText.value) Icons.Filled.Info else Icons.Filled.Create,
                contentDescription = ""
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isText.value) Box(
                modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
            ) {
                Text("按住说话")
            }
            if (isText.value) Box(modifier = Modifier.weight(1f)) {
                ChatTextField(message, msgChange)
            }
            if (isText.value && message.isNotEmpty()) IconButton(onClick = { msgChange("") }) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }
            IconButton(onClick = { toggleEmoji() }) {
                Icon(Icons.Filled.ThumbUp, contentDescription = "")
            }
            IconButton(onClick = { toggleOperation() }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "")
            }
            IconButton(onClick = { sendMessage(message) }) {
                Icon(Icons.Filled.Send, contentDescription = "")
            }
        }
    }
}

@Composable
fun ChatTextField(message: String, msgChange: (String) -> Unit) {
    BasicTextField(value = message,
        onValueChange = msgChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 16.sp, lineHeight = 20.sp
        ),
        minLines = 1,
        maxLines = 3,
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .border(2.dp, Color.Transparent, RoundedCornerShape(10.dp))
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                if (message.isEmpty()) {
                    Text(
                        "说点什么", color = Color.Gray, fontSize = 16.sp, lineHeight = 24.sp
                    )
                }
                innerTextField()  // 显示实际的文本输入框
            }
        })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatOperations() {
    FlowRow {
        OperationItem(title = "照片") {
            ImagePicker(maxSize = 1, option = ImagePickerOption.ImageOnly, onSelected = {}) {
                Icon(Icons.Filled.AccountBox, contentDescription = "")
            }
        }
        OperationItem(title = "语音通话") {
            Icon(Icons.Filled.Call, contentDescription = "")
        }
        OperationItem(title = "视频通话") {
            Icon(Icons.Filled.Home, contentDescription = "")
        }
        OperationItem(title = "位置") {
            Icon(Icons.Filled.LocationOn, contentDescription = "")
        }
        OperationItem(title = "录制视频") {
            Icon(Icons.Filled.PlayArrow, contentDescription = "")
        }
    }
}

@Composable
fun OperationItem(title: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.25f)
            .padding(vertical = 20.dp),
//        contentAlignment = Alignment.Center
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
            content()
        }
        Text(title)
    }
}

@Composable
fun ChatDetailTopBar(
    name: String,
    avatar: String,
    backButton: () -> Unit,
    goProfile: () -> Unit,
    goSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
            .padding(top = statusBarHeight), verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton {
            backButton()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { goProfile() },
            horizontalArrangement = Arrangement.Center
        ) {
            Avatar(url = avatar, size = 30.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(name, style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
        }
        IconButton(onClick = { goSettings() }) {
            Icon(Icons.Filled.Settings, contentDescription = "")
        }
    }
}