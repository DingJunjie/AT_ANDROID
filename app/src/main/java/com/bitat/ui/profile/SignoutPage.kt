package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.login.LoginCaptcha
import com.bitat.viewModel.SignoutViewModel


@Composable
fun SignoutPage(navHostController: NavHostController,viewModelProvider: ViewModelProvider) {

    val vm: SignoutViewModel = viewModelProvider[SignoutViewModel::class]
    val state = vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.setPhone(UserStore.userInfo.phone)
    }

    Scaffold(topBar = {
        CommonTopBar(title = stringResource(id = R.string.setting_signout_apply),
            backFn = { navHostController.popBackStack() },
            paddingStatus = true)
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.fillMaxWidth().height(30.dp))
            Text(modifier = Modifier.fillMaxWidth().padding(10.dp),
                text = stringResource(R.string.setting_signout_auth_hint),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.fillMaxWidth().height(30.dp))
            Column(modifier = Modifier.padding(start = 30.cdp, end = 30.cdp)) {

                LoginCaptcha(state.value.phone, state.value.captch, onPhoneChange = {
                    vm.setPhone(it)
                }, onCaptchaChange = {
                    vm.setCaptch(it)
                }, timerCount = 60, getCaptcha = {})

                Spacer(modifier = Modifier.fillMaxWidth().height(30.dp))
                Button(modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = state.value.isNext,
                    onClick = {
                        AtNavigation(navHostController).navigateToCancelAgreementPage()
                    }) {
                    Text("下一步")
                }
            }

        }
    }
}