package com.bitat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AtPopup(visible: Boolean, onClose: () -> Unit) {
    Popup(visible = visible, onClose = onClose) {
        AtLayout()
    }
}

@Composable
fun AtLayout() {
    val txt = remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = txt.value, onValueChange = {
            txt.value = it
        }, singleLine = true, modifier = Modifier.fillMaxWidth(0.9f))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)) {
            Avatar("https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            AtOptItem("艾特到", Icons.Filled.Create) {}
            AtOptItem("艾特到", Icons.Filled.Create) {}
            AtOptItem("艾特到", Icons.Filled.Create) {}
            AtOptItem("艾特到", Icons.Filled.Create) {}
            AtOptItem("艾特到", Icons.Filled.Create) {}
        }
    }
}

@Composable
fun AtOptItem(title: String, icon: ImageVector, tapFn: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 5.dp)) {
        IconButton(onClick = tapFn) {
            Icon(icon, contentDescription = "")
        }
        Text(title)
    }
}