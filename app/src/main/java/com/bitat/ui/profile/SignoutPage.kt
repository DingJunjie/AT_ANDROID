package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ui.component.CommonTopBar


@Composable
fun SignoutPage(navHostController: NavHostController) {
    Scaffold(topBar = {
        CommonTopBar(
            title = stringResource(id = R.string.setting_signout),
            backFn = { navHostController.popBackStack() })
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

        }
    }
}