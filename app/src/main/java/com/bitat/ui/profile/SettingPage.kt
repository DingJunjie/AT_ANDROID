package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.config.SettingCfg
import com.bitat.ext.cdp
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.white

/**
 *    author : shilu
 *    date   : 2024/9/2  16:47
 *    desc   :
 */
@Composable
fun SettingPage(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    Scaffold() { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)) {
            CommonTopBar(title = "设置", backFn = { navController.popBackStack() })
            Box(modifier = Modifier.fillMaxSize().weight(1f)
                .background(MaterialTheme.colorScheme.background)) {
                LazyColumn {

                    items(SettingCfg.getSettingMenu()) { item ->
                        SettingItem(item, onClick = { index ->
                            when (index) { //                                1 -> {}
                                //                                2 -> {}
                            }

                        })
                    }
                }
                Button(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.BottomCenter).padding(bottom = 68.cdp+WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
                    onClick = {}) {
                    Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        SvgIcon(path = "svg/login-out.svg",
                            contentDescription = "loginouticon",
                            modifier = Modifier.size(60.cdp),)
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
    }
}