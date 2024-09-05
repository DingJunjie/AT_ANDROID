package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.config.SettingCfg
import com.bitat.ui.component.CommonTopBar

/**
 *    author : shilu
 *    date   : 2024/9/5  16:50
 *    desc   :
 */

@Composable
fun AccountSecurePage(navHostController: NavHostController) {
    Scaffold(topBar = {
        CommonTopBar(modifier = Modifier,
            title = stringResource(id = R.string.setting_account_secure),
            backFn = { navHostController.popBackStack() },
            isBg = true,
            padingStatus = true)
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SettingCfg.getAccountMenu().forEach {
                SettingItem(it) {

                }
            }
        }

    }
}