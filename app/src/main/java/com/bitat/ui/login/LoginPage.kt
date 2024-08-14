package com.bitat.ui.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.amap.api.maps.MapsInitializer
import com.bitat.MainCo
import com.bitat.R
import com.bitat.ui.common.DialogState
import com.bitat.ui.common.rememberDialogState
import com.bitat.state.LoginState
import com.bitat.state.LoginType
import com.bitat.state.OneClickState
import com.bitat.style.FontStyle
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.rememberAsyncAssetPainter
import com.bitat.viewModel.LoginViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginPage(navHostController: NavHostController, navigateToHome: () -> Unit) {
    val viewModel: LoginViewModel = viewModel()
    val loginState by viewModel.loginState.collectAsState()
    val oneClickState by viewModel.oneClickState.collectAsState()
    val dialog = rememberDialogState()

    val timerCount by viewModel.timerDuration.collectAsState()
    val ctx = LocalContext.current

    viewModel.initOneClick(LocalContext.current)

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    if (oneClickState.signing) { // TODO: 登录中
    }

    if (loginState.isShowDialog) {
        dialog.show(title = stringResource(R.string.captcha),
            content = stringResource(id = R.string.count_down, timerCount.toString()),
            onOk = {
                viewModel.hideDialog()
            },
            onCancel = {
                viewModel.hideDialog()
            })
    }

    if (oneClickState.signed) { // 一键登录
        CuLog.debug(CuTag.Login, "into one login success")
        navigateToHome()
    }

    if (loginState.isShowToast) {
        ShowToast(content = loginState.toastContent, context = LocalContext.current)

        LaunchedEffect(IO) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    viewModel.closeToast()
                }
            }, 2000L)
        }
    }

    if (loginState.isShowOneClickToast) {
        ShowToast(content = oneClickState.errorMsg, context = LocalContext.current)

        LaunchedEffect(IO) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    viewModel.closeOneClickToast()
                }
            }, 2000L)
        }
    }

    MapsInitializer.updatePrivacyShow(LocalContext.current, true, true)

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()

        .bringIntoViewRequester(bringIntoViewRequester).onFocusChanged { focusState ->
            if (focusState.isFocused) {
                MainCo.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }.imePadding()) {
        Column(modifier = Modifier.fillMaxHeight().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.width(100.dp).padding(top = 80.dp)) {
                Image(painter = rememberAsyncAssetPainter("logo.jpg"), contentDescription = "")
            }


            Row(horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.88f)) {
                LoginMain(loginState = loginState,
                    oneClickState = oneClickState,
                    viewModel = viewModel,
                    loginType = loginState.type,
                    timerCount = timerCount,
                    loginFun = {
                        login(viewModel, dialog, successFn = {

                            navigateToHome()
                        })
                    })
            }


            Box(modifier = Modifier.height(100.dp)) {
                TextButton(onClick = {
                    viewModel.toggleAccept(!loginState.accepted)
                }) {
                    PolicyAndPolitics(accepted = loginState.accepted, toggleAccept = {
                        viewModel.toggleAccept(it)
                        MapsInitializer.updatePrivacyAgree(ctx, it)
                    })
                }

            }
        }
    }
}

@Composable
fun LoginMain(loginState: LoginState, viewModel: LoginViewModel, oneClickState: OneClickState, timerCount: Int, loginType: LoginType, loginFun: () -> Unit) {
    Column(verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color(0xfffbfbfb)).padding(bottom = 100.dp)) {
        if (loginType == LoginType.OneClick && oneClickState.initialized && oneClickState.supported && oneClickState.enabled) {
            OneClickWidget(phone = oneClickState.phoneNumber) {
                viewModel.oneClick()
            }
        } else {
            Column(modifier = Modifier.padding(top = 60.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                LoginCaptcha(loginState.phone, loginState.captcha, onPhoneChange = {
                    viewModel.updatePhone(it)
                }, onCaptchaChange = {
                    viewModel.updateCaptcha(it)
                }, timerCount, getCaptcha = {
                    if (timerCount > 0) {
                        viewModel.showDialog()
                    } else viewModel.setTimer()
                })
                LoginButtons(canLogin = viewModel.enableLogin(),
                    canOneClick = oneClickState.supported && oneClickState.initialized,
                    login = loginFun,
                    oneClick = {
                        viewModel.switchLoginType(LoginType.OneClick)
                    },
                    disableClickFn = {
                        viewModel.closeToast()
                        viewModel.showToast()
                    },
                    disableOneClickFn = {
                        viewModel.closeOneClickToast()
                        viewModel.showOneClickToast()
                    })
            }
        }
        if (loginState.type == LoginType.OneClick) {
            TextButton(onClick = {
                viewModel.switchLoginType(LoginType.Captcha)
            }, modifier = Modifier.align(Alignment.End)) {
                Text(stringResource(id = R.string.login_with_other_number))
            }
        }
    }


}

@Composable
fun CountDownContent(timerCount: Int) {
    return if (timerCount == 0) {
        Text(stringResource(R.string.get_captcha))
    } else {
        Text(stringResource(R.string.count_down, timerCount.toString()))
    }
}

@Composable
fun LoginCaptcha(phone: String, captcha: String, onPhoneChange: (String) -> Unit, onCaptchaChange: (String) -> Unit, timerCount: Int, getCaptcha: () -> Unit) {
    val kc = LocalSoftwareKeyboardController.current
    val callback = {
        kc?.show()
    }

    Column(

        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 10.dp)
            .border(width = 0.dp, color = Color.Transparent, shape = RoundedCornerShape(20.dp))) {
            OutlinedTextField(value = phone,
                onValueChange = onPhoneChange,
                isError = false,
                enabled = true,
                label = {
                    Text(stringResource(id = R.string.input_phone))
                },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xffeeeeee),
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedLabelColor = Color(0xff999999)),
                leadingIcon = {
                    Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                        TextButton(
                            onClick = {},
                        ) {
                            Text("+86")
                        }
                    }
                },
                keyboardActions = KeyboardActions(onDone = {}),
                modifier = Modifier.border(width = 0.dp,
                        color = Color.Transparent,
                        shape = CircleShape).padding(horizontal = 10.dp).fillMaxWidth()
                    .background(color = Color(0xfff5f5f5), CircleShape))
        }


        Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding()
            .border(width = 0.dp, color = Color.Transparent, shape = RoundedCornerShape(20.dp))) {
            OutlinedTextField(value = captcha,
                onValueChange = onCaptchaChange,
                label = {
                    Text(stringResource(id = R.string.input_capture))
                },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xffeeeeee),
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedLabelColor = Color(0xff999999)),
                trailingIcon = {
                    TextButton(onClick = getCaptcha,
                        contentPadding = PaddingValues(end = 15.dp, start = 15.dp)) {
                        CountDownContent(timerCount)
                    }
                },
                modifier = Modifier.border(width = 0.dp,
                        color = Color.Transparent,
                        shape = CircleShape).padding(horizontal = 10.dp).fillMaxWidth()
                    .background(color = Color(0xfff5f5f5), CircleShape),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    callback()
                }))
        }
    }
}

fun login(viewModel: LoginViewModel, dialog: DialogState, successFn: () -> Unit) {
    MainCo.launch {
        viewModel.loginByCaptcha(successFn, errorFn = {
            dialog.show("Login Error", content = "登录错误 $it")
        })
    }
}

@Composable
fun OneClickWidget(phone: String, clickFun: () -> Unit) {
    Column {
        TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = phone, fontSize = 20.sp, color = Color.Black, letterSpacing = 2.sp)
        }
        LoginButton(content = stringResource(id = R.string.one_click),
            clickFun,
            enabled = true,
            widthPercentage = 0.88f)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginButtons(
    canLogin: Boolean = false,
    canOneClick: Boolean = false,
    login: () -> Unit,
    oneClick: () -> Unit,
    disableClickFn: (() -> Unit) = {},
    disableOneClickFn: (() -> Unit) = {},
) {
    FlowRow(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(top = 10.cdp, bottom = 10.cdp, start = 10.cdp, end = 10.cdp)
            .fillMaxWidth()) {
        LoginButton(
            content = stringResource(id = R.string.login_register),
            clickFun = login,
            enabled = canLogin,
            disableClickFun = disableClickFn,
        )
        LoginButton(
            content = stringResource(id = R.string.one_click),
            clickFun = oneClick,
            enabled = canOneClick,
            disableClickFun = disableOneClickFn,
        )
    }
}

@Composable
fun LoginButton(content: String, clickFun: () -> Unit, enabled: Boolean, widthPercentage: Float = 0.45f, disableClickFun: (() -> Unit) = {}) {
    Button( //        enabled = enabled,
        colors = if (enabled) ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Blue,
        ) else ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.LightGray,
        ),
        onClick = { if (enabled) clickFun.invoke() else disableClickFun() },
        modifier = Modifier.fillMaxWidth(widthPercentage).height(88.cdp).pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (!enabled) {
                        disableClickFun()
                    }
                })
            }) {
        Text(content, fontSize = FontStyle.contentLargeSize, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ShowToast(content: String, context: Context) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PolicyAndPolitics(accepted: Boolean = true, toggleAccept: (Boolean) -> Unit) {
    FlowRow {
        Checkbox(checked = accepted, onCheckedChange = toggleAccept)
        Text("我已阅读并同意",
            modifier = Modifier.align(Alignment.CenterVertically),
            color = Color.Black)
        TextButton(onClick = {}) {
            Text("用户协议")
        }
        Text("和", modifier = Modifier.align(Alignment.CenterVertically), color = Color.Black)
        TextButton(onClick = {}) {
            Text("隐私政策")
        }
    }
}

@Composable
fun LoginOneClick(phone: String) {
    Column {
        Text(text = phone)
        Button(onClick = { /*TODO*/

        }) {

        }
    }
}