package com.bitat.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ui.component.BackButton
import com.bitat.viewModel.ChatViewModel

@Composable
fun ChatSettingsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ChatSettingTopBg()
        }
    }
}

@Composable
fun ChatSettingTopBg() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Row {
            BackButton {

            }
        }
    }
}