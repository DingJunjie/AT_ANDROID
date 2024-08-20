package com.bitat.ui.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption
import com.bitat.ui.profile.User
import com.bitat.ui.theme.Typography

@Composable
fun CommentTextField(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    placeholder: String,
    selectedImage: Uri = Uri.EMPTY,
    atUsers: List<UserBase1Dto>,
    tapAt: () -> Unit,
    selectUser: (UserBase1Dto) -> Unit,
    imageSelect: (Uri) -> Unit,
    sendComment: () -> Unit
) {
    val isShowEmoji = remember {
        mutableStateOf(false)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .background(Color.Transparent)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp)) {
            if (selectedImage != Uri.EMPTY) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .width(50.dp)
                ) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = "",
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .width(15.dp)
                            .height(15.dp)
                            .background(Color.Black)
                            .clickable {
                                imageSelect(Uri.EMPTY)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            }

            Column {
                if (atUsers.isNotEmpty()) Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.Yellow)
                ) {
                    Row {
                        atUsers.forEach { user ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectUser(user)
                                }) {
                                Avatar(url = user.profile, size = 25)
                                Text(
                                    user.nickname,
                                    style = Typography.bodySmall.copy(fontSize = 12.sp)
                                )
                            }
                        }
                    }
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
                                .padding(top = 5.dp, bottom = 5.dp)
                                .focusRequester(focusRequester = focusRequester),
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
                                    .padding(vertical = 2.dp),
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isShowEmoji.value = !isShowEmoji.value }) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = "")
                        }
                        IconButton(onClick = {
                            tapAt()
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "")
                        }
                        ImagePicker(
                            maxSize = 1,
                            option = ImagePickerOption.ImageOnly,
                            onSelected = {
                                imageSelect(it.first())
                            }) {
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

                        focusRequester.requestFocus()
                    })
                }
            }
        }
    }
}


