package com.bitat.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bitat.repository.dto.resp.CollectPartDto
import com.bitat.state.CollectState
import com.bitat.ui.common.rememberAsyncPainter
import com.bitat.viewModel.CollectViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay

@Composable
fun CollectPopup(
    visible: Boolean,
    collectViewModel: CollectViewModel,
    collectState: CollectState,
    createCollection: (String) -> Unit,
    tapCollect: (CollectPartDto) -> Unit,
    onClose: () -> Unit
) {
    var textField by remember {
        mutableStateOf("")
    }

    collectViewModel.initMyCollections()

    Popup(visible = visible, onClose = onClose) {
        Column {
            Text("添加到")
            CollectionList(collections = collectState.collections, tapCollect = tapCollect)
        }

        Box(contentAlignment = Alignment.BottomCenter) {
            CollectTextField(value = textField, onChange = { textField = it }, onEnter = {
                createCollection(textField)
                textField = ""
            })
        }
    }
}

@Composable
fun CollectionList(collections: List<CollectPartDto>, tapCollect: (CollectPartDto) -> Unit) {
    LazyColumn {
        items(collections) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(5.dp)
                    .clickable {
                        tapCollect(item)
                    }
//                    .background(Color.Yellow)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .paint(
                                painter = rememberAsyncPainter(item.cover),
                                contentScale = ContentScale.Crop
                            )
                    )
                    Text(item.name, color = Color.Black)
                }
                IconButton(onClick = { }) {
                    Icon(
                        if (item.key == 0) Icons.Filled.CheckCircle else Icons.Filled.Refresh,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
fun CollectTextField(
    value: String, onChange: (String) -> Unit, onEnter: (String) -> Unit
) {
    BasicTextField(value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .height(36.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 16.sp, lineHeight = 36.sp, color = Color.White
        ),
        keyboardActions = KeyboardActions(onDone = {
            onEnter(value)
        }),
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .background(Color.Black)
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        "新建收藏夹", color = Color.Gray, fontSize = 16.sp, lineHeight = 24.sp
                    )
                }
                innerTextField()  // 显示实际的文本输入框
            }
        })
}

@Composable
fun CollectTips(visible: Boolean, y: Int, closeTip: () -> Unit, openPopup: () -> Unit) {
//    val opacity by animateFloatAsState(targetValue = if (visible) 1f else 0f, label = "")
    if (visible) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(40.dp)
                .padding(horizontal = 20.dp)
                .offset(y = y.minus(10).dp)
//                .alpha(opacity)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xff888888))
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "")
                    Text("收藏成功")
                }

                TextButton(onClick = { openPopup() }) {
                    Text("更换收藏夹")
                }
            }

        }
    }
}