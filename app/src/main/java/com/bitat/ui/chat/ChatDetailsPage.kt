package com.bitat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ui.common.CarmeraOpen
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.BackButton
import com.bitat.ui.discovery.CardView
import com.bitat.ui.theme.Typography
import com.bitat.viewModel.ChatDetailsViewModel

/**
 *    author : shilu
 *    date   : 2024/8/6  10:49
 *    desc   : 聊天页
 */

@Composable
fun ChatDetailsPage(navHostController: NavHostController) {

    val vm: ChatDetailsViewModel = viewModel()
    val state by vm.state.collectAsState()

    val chatInput = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChatDetailTopBar(
                name = "hello",
                avatar = "https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg",
                backButton = { navHostController.popBackStack() },
                goProfile = { /*TODO*/ }) {
            }
        },
        bottomBar = {
            ChatBottomContainer(message = chatInput.value) {
                chatInput.value = it
            }
        }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            items(10) {
                MessageList()
            }
        }

//        ChatDetailTopBar(
//            name = "hello",
//            avatar = "",
//            backButton = { /*TODO*/ },
//            goProfile = { /*TODO*/ }) {
//        }
    }
}

@Composable
fun MessageList() {
    Surface(shape = RoundedCornerShape(30.dp)) {
        SenderMessage(message = "hello world")
    }
}

@Composable
fun ChatBottomContainer(message: String, msgChange: (String) -> Unit) {
    val optShow = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        ChatInputField(message, msgChange)
        ChatOperations()
    }
}

@Composable
fun ChatInputField(message: String, msgChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Info, contentDescription = "")
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ChatTextField(message, msgChange)
            }
            IconButton(onClick = { msgChange("") }) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.ThumbUp, contentDescription = "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Send, contentDescription = "")
            }
        }
    }
}

@Composable
fun ChatTextField(message: String, msgChange: (String) -> Unit) {
    BasicTextField(
        value = message,
        onValueChange = msgChange,
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 16.sp, lineHeight = 36.sp
        ),
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .border(2.dp, Color.Transparent, RoundedCornerShape(10.dp))
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                if (message.isEmpty()) {
                    Text(
                        "说点什么",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
                innerTextField()  // 显示实际的文本输入框
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatOperations() {
    FlowRow {
        OperationItem()
        OperationItem()
        OperationItem()
        OperationItem()
        OperationItem()
    }
}

@Composable
fun OperationItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.25f)
            .padding(vertical = 20.dp)
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Info, contentDescription = "")
        }
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
            .padding(top = statusBarHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton {
            backButton()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { goProfile() }, horizontalArrangement = Arrangement.Center
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