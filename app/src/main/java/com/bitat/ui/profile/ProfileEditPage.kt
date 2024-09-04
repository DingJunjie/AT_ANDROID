package com.bitat.ui.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.store.UserStore
import com.bitat.state.GENDER
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.component.Popup
import com.bitat.utils.ScreenUtils
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()
    var maxLength = 84

    val dataPickerState = rememberDatePickerState()

    val isShowPicker = remember {
        mutableStateOf(false)
    }

    val isShowGender = remember {
        mutableStateOf(false)
    }

    val isShowAvatarOpt = remember {
        mutableStateOf(false)
    }

    val nameTextValue = remember {
        mutableStateOf(UserStore.userInfo.nickname)
    }

    val introTextValue = remember {
        mutableStateOf(UserStore.userInfo.introduce)
    }

    val addressTextValue = remember {
        mutableStateOf(UserStore.userInfo.address)
    }

    val coroutineScope = rememberCoroutineScope()
    val toast = rememberToastState()
    val focusManager = LocalFocusManager.current

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color.Cyan)) {
            Spacer(modifier = Modifier.fillMaxWidth().height(statusBarHeight))
            CommonTopBar(
                tint = Color.White,
                modifier = Modifier.padding(top = statusBarHeight),
                title = "",
                backFn = { navHostController.popBackStack() }, //                endButton = {
                //                    Text(text = "提交",
                //                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                //                        modifier = Modifier.padding(end = 10.dp).clickable {})
                //                }
            )
            Box(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.8f)
                .padding(start = ScreenUtils.screenWidth.times(0.05).dp, top = 150.dp)
                .clip(RoundedCornerShape(61.cdp)).background(Color.White),
                contentAlignment = Alignment.TopCenter

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()) {

                    Text("修改形象", modifier = Modifier.padding(top = 60.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("用户名", modifier = Modifier.padding(horizontal = 20.dp).width(70.dp))
                        BasicTextField(value = nameTextValue.value,
                            singleLine = true,
                            onValueChange = {
                                nameTextValue.value = it
                            },
                            keyboardActions = KeyboardActions(onDone = {
                                UserStore.updateNickname(nameTextValue.value, onSuccess = {
                                    vm.showSuccess(true)
                                    coroutineScope.launch {
                                        delay(1000)
                                        vm.showSuccess(false)
                                    }

                                }, onError = {
                                    vm.showFail(true)
                                    coroutineScope.launch {
                                        delay(1000)
                                        vm.showFail(false)
                                    }
                                })

                                focusManager.clearFocus()
                            }) { },
                            modifier = Modifier.weight(1f))
                    }

                    Column(modifier = Modifier.fillMaxWidth(0.8f).weight(1f),
                        verticalArrangement = Arrangement.SpaceEvenly) {
                        Row {
                            Text("性别",
                                modifier = Modifier.padding(horizontal = 20.dp).width(70.dp))
                            Text(GENDER.toUIContent(state.user.gender),
                                modifier = Modifier.weight(1f).clickable {
                                    focusManager.clearFocus()
                                    isShowGender.value = true
                                })
                        }

                        Row {
                            Text("破壳日",
                                modifier = Modifier.padding(horizontal = 20.dp).width(70.dp))
                            Text(TimeUtils.timeToYMD(state.user.birthday),
                                modifier = Modifier.clickable {
                                    focusManager.clearFocus()
                                    isShowPicker.value = true
                                })
                        }

                        Row {
                            Text("说一说",
                                modifier = Modifier.padding(horizontal = 20.dp).width(70.dp))
                            Box() {

                            }
                            BasicTextField(value = introTextValue.value,
                                onValueChange = { //                                if (it.length <= maxLength) {
                                    //                                    introTextValue.value = it
                                    //                                }
                                    introTextValue.value = it
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    UserStore.updateIntroduce(introTextValue.value, onSuccess = {
                                        vm.showSuccess(true)
                                        coroutineScope.launch {
                                            delay(1000)
                                            vm.showSuccess(false)
                                        }

                                    }, onError = {
                                        vm.showFail(true)
                                        coroutineScope.launch {
                                            delay(1000)
                                            vm.showFail(false)
                                        }
                                    })

                                    focusManager.clearFocus()
                                }) { },
                                modifier = Modifier.weight(1f))
                        }

                        //                        Row {
                        //                            Text("地址",
                        //                                modifier = Modifier.padding(horizontal = 20.dp).width(70.dp))
                        //                            BasicTextField(value = addressTextValue.value, onValueChange = {
                        //                                addressTextValue.value = it
                        //                            }, keyboardActions = KeyboardActions(onDone = {
                        //                                UserStore.updateAddress(addressTextValue.value)
                        //                            }), modifier = Modifier.weight(1f))
                        //                        }

                        //                        Row {
                        //                            Text("学校",
                        //                                modifier = Modifier.padding(horizontal = 20.dp).width(70.dp))
                        //                            BasicTextField(value = "我的名字",
                        //                                onValueChange = {},
                        //                                modifier = Modifier.weight(1f))
                        //                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.8f)
                .padding(start = ScreenUtils.screenWidth.times(0.05).dp, top = 150.dp)
                .background(Color.Transparent), contentAlignment = Alignment.TopCenter) {
                Box(modifier = Modifier.offset(y = (-50).dp)) {
                    AvatarWithShadow(url = UserStore.userInfo.profile, size = 80) {
                        isShowAvatarOpt.value = true
                    }
                }
            }

        }
        if (state.updateFlag > 0) Text(text = "")

        if (state.showSuccess) toast.show(stringResource(R.string.update_success))

        if (state.showFail) toast.show(stringResource(R.string.update_failed))

    }

    GenderPopup(isShowGender.value, { isShowGender.value = false }, {
        UserStore.updateGender(it, onSuccess = {
            vm.showSuccess(true)
            coroutineScope.launch {
                delay(1000)
                vm.showSuccess(false)
            }
            isShowGender.value = false
        }, onError = {
            vm.showFail(true)
            coroutineScope.launch {
                delay(1000)
                vm.showFail(false)
            }
            isShowGender.value = false
        })
    })

    BirthdayPopup(isShowPicker.value, dataPickerState, { isShowPicker.value = false }) {
        CuLog.info(CuTag.Profile, dataPickerState.selectedDateMillis.toString())
        dataPickerState.selectedDateMillis?.let {
            UserStore.updateBirthday(it, onSuccess = {
                vm.showSuccess(true)
                coroutineScope.launch {
                    delay(1000)
                    vm.showSuccess(false)
                }
                isShowPicker.value = false
            }, onError = {
                vm.showFail(true)
                coroutineScope.launch {
                    delay(1000)
                    vm.showFail(true)
                }
                isShowPicker.value = false
            })

        }

    }

    AvatarPopup(isShowAvatarOpt.value, { isShowAvatarOpt.value = false }, galleryFn = {
        UserStore.updateAvatar(it, onSuccess = {
            isShowAvatarOpt.value = false
            vm.showSuccess(true)
            coroutineScope.launch {
                delay(1000)
                vm.showSuccess(false)
            }

        }, onError = {
            isShowAvatarOpt.value = false
            vm.showFail(false)
            coroutineScope.launch {
                delay(1000)
                vm.showFail(false)
            }

        })

    }) {

    }
}

@Composable
fun AvatarPopup(visible: Boolean, onClose: () -> Unit, galleryFn: (Uri) -> Unit = {}, cameraFn: (Uri) -> Unit) {
    Popup(visible = visible, onClose = { onClose() }) {
        Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
            ImagePicker(maxSize = 1, option = ImagePickerOption.ImageOnly, onSelected = {
                galleryFn(it.first())
            }) {
                Text("相册中选择")
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("拍照")
        }
        Row(modifier = Modifier.fillMaxWidth().height(50.dp).clickable { onClose() }) {
            Text("取消")
        }
    }
}

@Composable
fun GenderPopup(visible: Boolean, onClose: () -> Unit, tapFn: (GENDER) -> Unit) {
    Popup(visible = visible, onClose = { onClose() }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            GENDER.entries.forEach {
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    tapFn(it)
                }.height(50.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(it.toUIContent())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayPopup(visible: Boolean, dataPickerState: DatePickerState, onClose: () -> Unit, tapFn: (Long) -> Unit) {
    Popup(visible = visible, onClose = { onClose() }) {
        DatePicker(state = dataPickerState)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (dataPickerState.selectedDateMillis != UserStore.userInfo.birthday) IconButton(
                onClick = {
                    tapFn(dataPickerState.selectedDateMillis ?: UserStore.userInfo.birthday)
                }) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "")
            }
        }
    }
}