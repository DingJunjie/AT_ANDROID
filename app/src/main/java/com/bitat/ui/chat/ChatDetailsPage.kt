package com.bitat.ui.chat

import android.net.Uri
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.ext.Density
import com.bitat.ext.clickableWithoutRipple
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.CHAT_Picture
import com.bitat.repository.consts.CHAT_Recall
import com.bitat.repository.consts.CHAT_Reply
import com.bitat.repository.consts.CHAT_Text
import com.bitat.repository.consts.CHAT_Time
import com.bitat.repository.consts.CHAT_Video
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.singleChat.GetNewMessage
import com.bitat.repository.singleChat.GetRooms
import com.bitat.repository.singleChat.SetTop
import com.bitat.repository.singleChat.SingleChatHelper
import com.bitat.repository.singleChat.SingleChatHelper.singleChatUiFlow
import com.bitat.repository.singleChat.TcpHandler.newMsgFlow
import com.bitat.router.NavigationItem
import com.bitat.state.ReplyMessageParams
import com.bitat.state.VideoMessageParams
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption
import com.bitat.ui.common.SingleFuncCamera
import com.bitat.ui.common.VideoPreview
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.BackButton
import com.bitat.ui.component.EmojiTable
import com.bitat.ui.theme.Typography
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.ChatDetailsViewModel
import com.bitat.viewModel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.serialization.json.Json

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

    LaunchedEffect(Dispatchers.Default) {
        singleChatUiFlow.collect {
            when (it) {
                is GetRooms -> {
                }

                is SetTop -> {
                }

                is GetNewMessage -> {
                    if (chatState.currentRoom.otherId == it.msg.otherId) {
                        vm.getNewMessage(it.msg)
                    }
                }
            }
        }

    }

    val currentPointerOffset = remember {
        mutableStateOf(Offset(0f, 0f))
    }

    val msgHeight = remember {
        mutableStateListOf<Offset>()
    }

    LaunchedEffect(IO) {
        vm.getMessage(chatState.currentRoom.otherId)

        msgHeight.clear()
        msgHeight.addAll(Array(state.messageList.size) { Offset(x = 0f, 0f) })
    }

    val currentItemIndex = remember {
        mutableIntStateOf(0)
    }

    val canTouch = remember {
        mutableStateOf(true)
    }

    val chatListScrollState = rememberLazyListState()
    val isLoadingMore = remember {
        mutableStateOf(false)
    }

    val isRecording = remember {
        mutableStateOf(false)
    }

    val currentVideoUri = remember {
        mutableStateOf(Uri.EMPTY)
    }

    val isShowVideo = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(chatListScrollState) {
        snapshotFlow { chatListScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (isLoadingMore.value) return@collect
                val isBottom =
                    lastVisibleIndex == chatListScrollState.layoutInfo.totalItemsCount - 1
                if (isBottom) {
                    vm.getMessage(chatState.currentRoom.otherId, pageNo = state.currentPage) {
                        isLoadingMore.value = false
                    }
                    val diff = state.messageList.size - msgHeight.size
                    msgHeight.addAll(Array(diff) { Offset(x = 0f, 0f) })
                }
            }
    }

    Scaffold(topBar = {
        ChatDetailTopBar(name = if (chatState.currentRoom.nickname == "") "" else chatState.currentRoom.nickname,
            avatar = if (chatState.currentRoom.profile == "") "" else chatState.currentRoom.profile,
            backButton = {
                chatVm.clearRoom()
                navHostController.popBackStack()
            },
            goProfile = { /*TODO*/ }, {
                navHostController.navigate(NavigationItem.ChatSettings.route).apply {

                }
            })
    }, bottomBar = {
        ChatBottomContainer(
            message = chatInput.value,
            replyMsg = state.replyMsg,
            nickname = chatState.currentRoom.nickname,
            {
                chatInput.value = it
            },
            {
                if (it.trim().isEmpty()) {
                    // 为空
                    return@ChatBottomContainer
                }
                vm.sendMessage(chatState.currentRoom.otherId, 1, it) { that ->
                    chatVm.updateRoomInfo(that)
                    chatInput.value = ""
                }
            },
            {
                vm.sendPicture(chatState.currentRoom.otherId, it) { that ->
                    chatVm.updateRoomInfo(that)
                    chatInput.value = ""
                }
            },
            {
                isRecording.value = true
            })
    }, modifier = Modifier.imePadding()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .pointerInput(Unit) {
//                    detectTapGestures(onLongPress = {
////                        if (canTouch.value) {
//                        currentPointerOffset.value = it
//                        showMsgOpt.value = true
////                        }
//                    })
//                }
        ) {
            if (chatState.currentRoom.background != "") {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = QiNiuUtil.QINIU_CHAT_PREFIX + chatState.currentRoom.background,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            LazyColumn(
                state = chatListScrollState,
                reverseLayout = true,
                modifier = Modifier
                    .padding(padding)
            ) {
                itemsIndexed(state.messageList) { index, item ->
                    Box(modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = {
//                        if (canTouch.value) {
//                                currentPointerOffset.value = it
//                                showMsgOpt.value = true
//                        }
                                currentItemIndex.intValue = index
                                showMsgOpt.value = true
                            })
                        }
                        .onGloballyPositioned { coo ->
                            val pos = coo.boundsInParent()
                            if (index > msgHeight.size - 1) return@onGloballyPositioned
                            msgHeight[index] = pos.topLeft
                            println("第$index 个，位置是$pos")
                            println("第$index 个，位置是${pos.topLeft}")

                            println("当前msg height 数组是${msgHeight}")
                        }) {
                        if (item.status.toInt() == 1 || item.status.toInt() == 2) {
                            when (item.kind) {
                                CHAT_Text -> SenderMessage(item)
                                CHAT_Picture -> SenderImage(url = item.content)
                                CHAT_Video -> {
                                    val c = Json.decodeFromString<VideoMessageParams>(item.content)
                                    SenderVideo(c.cover, c.video)
                                }

                                CHAT_Recall -> {
                                    RecallMessage(
                                        nickname = chatState.currentRoom.nickname
                                    )
                                }

                                CHAT_Reply -> {
                                    val msg = Json.decodeFromString(
                                        ReplyMessageParams.serializer(),
                                        item.content
                                    )
                                    SenderReplyMessage(
                                        message = msg.content,
                                        replyContent = msg.replyMsg,
                                        replyMsgKind = msg.kind
                                    )
                                }

                                CHAT_Time -> {
                                    TimeMessage(timestamp = item.time)
                                }
                            }
                        } else if (item.status.toInt() == -1) {
                            when (item.kind) {
                                CHAT_Text -> RecipientMessage(item)
                                CHAT_Picture -> RecipientImage(url = item.content)
                                CHAT_Video -> {
                                    val c = Json.decodeFromString<VideoMessageParams>(item.content)
                                    RecipientVideo(c.cover, c.video)
                                }

                                CHAT_Recall -> {
                                    RecallMessage(nickname = chatState.currentRoom.nickname)
                                }

                                CHAT_Reply -> {
                                    val msg = Json.decodeFromString(
                                        ReplyMessageParams.serializer(),
                                        item.content
                                    )
                                    RecipientReplyMessage(
                                        message = msg.content,
                                        replyContent = msg.replyMsg,
                                        replyMsgKind = msg.kind
                                    )
                                }

                                CHAT_Time -> {
                                    TimeMessage(timestamp = item.time)
                                }

                            }
                        }
                    }
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
            .clickableWithoutRipple {
                showMsgOpt.value = false
                canTouch.value = false
            }) {
            Surface(
                shape = RoundedCornerShape(10.dp),
//                modifier = Modifier.offset(
//                    currentPointerOffset.value.x.div(Density).toInt().dp,
//                    currentPointerOffset.value.y.div(Density).toInt().dp
//                .div(Density).toInt().dp
//                )
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .offset(
                        x = ScreenUtils.screenWidth.times(0.05).dp,
//                        y = currentPointerOffset.value.y
//                            .div(Density)
//                            .toInt().dp
                        y = msgHeight[currentItemIndex.intValue].y.div(Density).dp
                    )
            ) {
                ChatMessageOpt(
                    msg = state.messageList[currentItemIndex.intValue],
                    reply = {
                        vm.setReplyMsg(state.messageList[currentItemIndex.intValue])
                        showMsgOpt.value = false
                    },
                    copy = {
                        showMsgOpt.value = false
                    }, recall = {
                        vm.recallMessage(state.messageList[currentItemIndex.intValue])
                        showMsgOpt.value = false
                    }, delete = {
                        vm.deleteMessage(state.messageList[currentItemIndex.intValue])
                        msgHeight.removeAt(currentItemIndex.intValue)
                        showMsgOpt.value = false
                    })
            }
        }

    if (isRecording.value) Box(modifier = Modifier.fillMaxSize()) {
        SingleFuncCamera(ImagePickerOption.VideoOnly) {
            currentVideoUri.value = it
            isShowVideo.value = true
            isRecording.value = false
        }
    }

    if (isShowVideo.value) Box(modifier = Modifier.fillMaxSize()) {
        VideoPreview(uri = currentVideoUri.value) {
            isShowVideo.value = false
            vm.sendVideo(chatState.currentRoom.otherId, it) { that ->
                chatVm.updateRoomInfo(that)
                chatInput.value = ""
            }
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
    replyMsg: SingleMsgPo?,
    nickname: String,
    msgChange: (String) -> Unit,
    sendMessage: (String) -> Unit,
    sendPicture: (Uri) -> Unit,
    sendVideo: () -> Unit
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
            .padding(top = 20.dp)
            .background(Color.White)
    ) {
        if (replyMsg != null) Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        ) {
            Text("回复$nickname:")
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
        if (optShow.value) ChatOperations(sendPicture = {
            sendPicture(it)
            optShow.value = false
        }, sendVideo = {
            sendVideo()
            optShow.value = false
        })
        Box(modifier = Modifier.height(20.dp))
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
fun ChatOperations(
    sendPicture: (Uri) -> Unit,
    sendVideo: () -> Unit
) {
    FlowRow {
        OperationItem(title = "照片") {
            ImagePicker(maxSize = 1, option = ImagePickerOption.ImageOnly, onSelected = {
                sendPicture(it.first())
            }) {
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
            IconButton(onClick = sendVideo) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = ""
                )
            }
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