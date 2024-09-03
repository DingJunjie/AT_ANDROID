package com.bitat.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.config.SettingCfg
import com.bitat.ext.cdp
import com.bitat.repository.po.SettingMenuPo
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.theme.lineColor

/**
 *    author : shilu
 *    date   : 2024/9/2  14:41
 *    desc   : 系统设置
 */

@Composable
fun SystemSetting(
    viewModelProvider: ViewModelProvider,
    navController: NavHostController,
    onBack: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(statusBarHeight))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AvatarWithShadow(
                modifier = Modifier,
                url = UserStore.userInfo.profile,
                size = 60,
                needPadding = true
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.padding(top = 15.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(UserStore.userInfo.nickname, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(5.dp))
                Text(UserStore.userInfo.id.toString(), style = MaterialTheme.typography.bodySmall)
            }
        }
        Divider(
            modifier = Modifier.height(dimensionResource(R.dimen.line_height)),
            color = lineColor
        )

        LazyColumn {
            items(SettingCfg.getProfileMenu()) { item ->
                SettingItem(item, onClick = { itemIndex ->
                    when (itemIndex) {
                        1 -> { //观看历史

                        }

                        2 -> { // 反馈

                        }

                        3 -> { // 设置
                            AtNavigation(navController).navigateToSettingPage()
                            onBack()
                        }
                    }
                })


            }
        }
    }
}

@Composable
fun SettingItem(settingPo: SettingMenuPo, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .height(100.cdp)
            .clickable { onClick(settingPo.itemIndex) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        SvgIcon(path = settingPo.icon, contentDescription = "", modifier = Modifier.size(40.cdp))
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = settingPo.content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF6A6A6A)
            )
        )
        if (settingPo.showRight) {
            SvgIcon(
                path = "svg/arrow-right.svg",
                contentDescription = "backicon",
                modifier = Modifier.size(30.cdp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))

    }
}