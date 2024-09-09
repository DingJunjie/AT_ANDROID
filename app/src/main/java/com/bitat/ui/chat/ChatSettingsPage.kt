package com.bitat.ui.chat

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.amap.api.services.route.Navi
import com.bitat.R
import com.bitat.router.NavigationItem
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption
import com.bitat.ui.component.BackButton
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogMoreViewModel
import com.bitat.viewModel.ChatViewModel
import java.security.spec.EllipticCurve

@Composable
fun ChatSettingsPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val chatVm = viewModelProvider[ChatViewModel::class]
    val chatState by chatVm.state.collectAsState()

    val oprVm = viewModelProvider[BlogMoreViewModel::class]
    val oprState = oprVm.state.collectAsState()

    var flag = remember {
        mutableStateOf(false)
    }

    Scaffold { padding ->
        Column() {
            ChatSettingTopBg(
                modifier = Modifier.padding(padding),
                background = chatState.currentRoom.background,
                avatar = chatState.currentRoom.profile,
                backFn = {
                    navHostController.popBackStack()
                }) {
                chatVm.changeBg(it) {
                    flag.value = !flag.value
                }
            }

            ChatSettings(
                isMuted = chatState.currentRoom.muted,
                isTop = chatState.currentRoom.top,
                isBlackList = false,
                switchMuted = {
                    chatVm.muteRoom(chatState.currentRoom.otherId, it)
                    flag.value = !flag.value
                },
                switchTop = {
                    chatVm.setTop(otherId = chatState.currentRoom.otherId, it)
                    flag.value = !flag.value
                },
                relationChange = {
                    flag.value = !flag.value
                },
                goHistory = {
                    navHostController.navigate(NavigationItem.ChatHistory.route)
                },
                goDetailSetting = {},
                clearAllMsg = {
                    chatVm.clearAllMessage(chatState.currentRoom.otherId)
//                    chatVm.updateRoomContent(cr!!.otherId)
                },
                feedback = {
                    oprVm.setUser(chatState.currentRoom.otherId)
                    navHostController.navigate(NavigationItem.ReportUser.route)
                }
            )
            if (flag.value) Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(1.dp)
            ) else Text("")
        }
    }
}

@Composable
fun ChatSettingTopBg(
    modifier: Modifier = Modifier,
    background: String,
    avatar: String,
    backFn: () -> Unit,
    switchBg: (Uri) -> Unit
) {
    Box {
        Surface(shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(
//                        bottomEnd = ScreenUtils.screenWidth.times(0.2).dp,
//                        bottomStart = ScreenUtils.screenWidth.times(0.2).dp
                        bottomStartPercent = 30, bottomEndPercent = 30
                    ), modifier = Modifier.clip(
                        shape = RoundedCornerShape(
                            bottomEnd = ScreenUtils.screenWidth.times(0.2).dp,
                            bottomStart = ScreenUtils.screenWidth.times(0.2).dp
                        )
                    )
                ) {
                    ImagePicker(maxSize = 1, option = ImagePickerOption.ImageOnly, onSelected = {
                        switchBg(it.first())
                    }) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight(),
                            model = if (background == "") "https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg" else QiNiuUtil.QINIU_CHAT_PREFIX + background,
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.offset(y = 40.dp)
                ) {
                    Avatar(
                        url = avatar,
                        modifier = Modifier
                            .border(width = 6.dp, color = Color.White, CircleShape)
                            .shadow(elevation = 3.dp, shape = CircleShape),
                        size = 80.dp
                    )
                }
            }
        }

        Row(modifier) {
            BackButton(tint = Color.White) {
                backFn()
            }

        }
    }
}

@Composable
fun ChatSettings(
    isMuted: Int = 0,
    isTop: Int = 0,
    isBlackList: Boolean = false,
    switchMuted: (Boolean) -> Unit,
    switchTop: (Boolean) -> Unit,
    relationChange: (Boolean) -> Unit,
    goHistory: () -> Unit = {},
    goDetailSetting: () -> Unit = {},
    clearAllMsg: () -> Unit = {},
    feedback: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable {
                    goHistory()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "查找聊天消息")
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 1.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("消息免打扰")
            Switch(checked = isMuted == 1, onCheckedChange = switchMuted)
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 1.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("置顶聊天")
            Switch(checked = isTop == 1, onCheckedChange = switchTop)
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 1.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("拉入黑名单")
            Switch(checked = isBlackList, onCheckedChange = relationChange)
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable {
                    clearAllMsg()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("清空聊天消息")
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable {
                    goDetailSetting()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("聊天设置")
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "")
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable {
                    feedback()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("投诉")
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "")
        }
    }
}