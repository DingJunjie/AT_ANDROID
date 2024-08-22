package com.bitat.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.ui.common.CarmeraOpen
import com.bitat.ui.discovery.CardView
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
//    Column(modifier = Modifier.fillMaxSize().padding(30.dp)) {
//        TextField(modifier = Modifier.fillMaxWidth().height(100.dp),
//            value = statte.chatMsg,
//            onValueChange = { vm.onChatChang(it) })
//
//        Button(onClick = { vm.sendClick(statte.chatMsg) }) {
//            Text("发送")
//        }
//
//
//        LaunchedEffect(Unit) { //            CarmeraOpen()
//        }
//
//    }
//    CardView()
}