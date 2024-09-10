package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.component.Popup
import com.bitat.ui.theme.hintTextColor
import com.bitat.viewModel.BlackListViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

@Composable
fun BlackListPage(navHostController: NavHostController) {
    val vm = viewModel(BlackListViewModel::class)
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit){
        //初始化数据
        vm.blackList()
    }

    var isConfirmShow by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        CommonTopBar(
            modifier = Modifier,
            title = stringResource(id = R.string.setting_blacklist),
            backFn = { navHostController.popBackStack() },
            isBg = true,
            padingStatus = true
        )
    }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .background(colorResource(id = R.color.gray_F0))
        ) {
            LazyColumn {
                items(state.myBlackList) { item ->
                    BlackListItem(item) {
                        vm.setCurrent(item)
                    }
                }
            }
        }
        state.currentUser?.let { user ->
            ConfirmPop(isConfirmShow, user, onConfirm = {
                vm.removeBlackList(user, onComplete = { result ->
                    ToastModel(
                        if (result) "操作成功" else "操作失败",
                        ToastModel.Type.Success
                    ).showToast()
                })
            }, onClose = {
                isConfirmShow = false
            })
        }
    }
}

@Composable
fun BlackListItem(user: UserBase1Dto, itemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        com.bitat.ui.component.Avatar(url = user.profile)
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text(text = user.nickname, style = MaterialTheme.typography.bodyLarge)
            Row {
                Text(
                    text = stringResource(id = R.string.blog_fans) + ":${user.fans}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = hintTextColor)
                )
                Spacer(
                    modifier = Modifier
                        .height(5.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.blog_follow) + ":${user.follows}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = hintTextColor)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
        )

        Box(
            modifier = Modifier
                .height(30.dp)
                .width(50.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = {
                    itemClick()
                }, indication = null, interactionSource = remember { MutableInteractionSource() })
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.setting_blacklist_remove)
            )
        }


    }
}

@Composable
fun ConfirmPop(
    visible: Boolean,
    user: UserBase1Dto,
    onConfirm: () -> Unit,
    onClose: () -> Unit
) {
    Popup(visible = visible, onClose = onClose) {
        Column {
            com.bitat.ui.component.Avatar(url = user.profile)
            Text(text = user.nickname)
            Spacer(
                modifier = Modifier
                    .height(10.dp)
                    .fillMaxWidth()
            )
            Text(text = stringResource(id = R.string.setting_blacklist_remove))
            Spacer(
                modifier = Modifier
                    .height(10.dp)
                    .fillMaxWidth()
            )
            Row {
                Button(
                    modifier = Modifier.background(color = colorResource(id = R.color.gray_ccc)),
                    onClick = {
                        onClose()
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }

                Button(modifier = Modifier, onClick = {
                    onConfirm()
                }, shape = RoundedCornerShape(16.dp)) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    }
}