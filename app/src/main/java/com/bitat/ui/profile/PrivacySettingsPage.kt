package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.repository.po.SettingMenuPo
import com.bitat.router.AtNavigation
import com.bitat.ui.component.CommonTopBar

/**
 *    author : shilu
 *    date   : 2024/9/10  10:33
 *    desc   :
 */

@Composable
fun PrivacySettingsPage(navHostController: NavHostController) {
    Scaffold(topBar = {
        CommonTopBar(
            modifier = Modifier,
            title = stringResource(id = R.string.setting_privacy),
            backFn = { navHostController.popBackStack() },
            isBg = true,
            paddingStatus = true
        )
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            SettingItem(
                SettingMenuPo(
                    showLeft = false,
                    itemIndex = 1,
                    content = stringResource(id = R.string.setting_blacklist)
                ), onClick = {
                    AtNavigation(navHostController).navigationToBlackList()
                })
        }
    }
}