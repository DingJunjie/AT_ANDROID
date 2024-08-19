package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommentTextField(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    sendComment: () -> Unit
) {
    val isShowEmoji = remember {
        mutableStateOf(false)
    }

    Surface(
//        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .background(Color.LightGray)
    ) {

        val keyboardController = LocalSoftwareKeyboardController.current
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Yellow)) {

            }
            Row {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = {
                            onValueChange(it)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .padding(top = 5.dp, bottom = 5.dp),
                        textStyle = MaterialTheme.typography.body1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { // 完成按键被点击时的回调
                            keyboardController?.hide() // 隐藏软键盘
                            // 这里可以执行你的逻辑
                        })
                    ) { innerTextField ->
                        Box(
                            Modifier
                                .border(1.dp, Color.Transparent, RoundedCornerShape(10.dp))
                                .padding(vertical = 2.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                placeholder,
                                color = Color.Gray,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )

                            innerTextField()  // 显示实际的文本输入框
                        }
                    }
                }

                Row {
                    IconButton(onClick = { isShowEmoji.value = !isShowEmoji.value }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Add, contentDescription = "")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.AccountBox, contentDescription = "")
                    }
                    if (textFieldValue.text.isNotEmpty()) IconButton(onClick = { sendComment() }) {
                        Icon(Icons.Filled.Send, contentDescription = "")
                    }
                }

            }

            if (isShowEmoji.value) Surface(modifier = Modifier.fillMaxWidth()) {
                EmojiTable(onTextAdded = {
                    val newContent = textFieldValue.text + it
                    onValueChange(
                        textFieldValue.copy(
                            text = newContent,
                            selection = TextRange(newContent.length)
                        )
                    )
                })
            }
        }
    }
}

