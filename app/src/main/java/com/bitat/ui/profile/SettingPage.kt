package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.AppConfig
import com.bitat.Local
import com.bitat.config.SettingCfg
import com.bitat.ext.cdp
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.component.Popup
import com.bitat.ui.theme.white
import com.bitat.viewModel.ProfileViewModel
import com.bitat.viewModel.SettingViewModel

/**
 *    author : shilu
 *    date   : 2024/9/2  16:47
 *    desc   :
 */
@Composable
fun SettingPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {

    val vm: SettingViewModel = viewModel()
    val state = vm.state.collectAsState()
    val profileVm = viewModelProvider[ProfileViewModel::class]
    Scaffold() { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)) {
            CommonTopBar(title = "设置", backFn = { navController.popBackStack() })
            Box(modifier = Modifier.fillMaxSize().weight(1f)
                .background(MaterialTheme.colorScheme.background)) {
                LazyColumn {

                    itemsIndexed(SettingCfg.getSettingMenu()) { index, item ->
                        SettingItem(item, onClick = { index ->
                            when (index) {
                                1 -> {
                                    AtNavigation(navController).navigateToAccountSecurePage()
                                }
                                2 -> {

                                }
                                4 -> AtNavigation(navController).navigateToCachePage()
                            }
                        })
                    }
                }
                Button(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.BottomCenter)
                    .padding(bottom = 68.cdp + WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()), onClick = {
                    vm.loginOut(onSuccess = { // 关闭tcp连接
                        TcpClient.close() // 清除用户数据
                        profileVm.clearVm()
                        TokenStore.cleanLogin()
                        UserStore.clearUser()
                        Local.navigateToLoginPage()
                    }, onError = { })
                }) {
                    Row(modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        SvgIcon(
                            path = "svg/login-out.svg",
                            contentDescription = "loginouticon",
                            modifier = Modifier.size(60.cdp),
                        )
                        Text(modifier = Modifier.fillMaxWidth().weight(1f),
                            text = "退出登录",
                            style = MaterialTheme.typography.bodyLarge.copy(color = white),
                            textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.fillMaxWidth().height(68.cdp))
                Spacer(modifier = Modifier.fillMaxWidth()
                    .height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                    .background(MaterialTheme.colorScheme.background))
            }

        }
        LoadingPopup(state.value.isDialogShow)
    }

}

@Composable
fun LoadingPopup(visible: Boolean) {
    Popup(visible = visible, onClose = { }) {
        Column(modifier = Modifier.size(150.dp)) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(20.dp).fillMaxWidth())
            Text(text = "正在处理中···")
        }
    }
}