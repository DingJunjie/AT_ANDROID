package com.bitat.viewModel

import android.content.Context
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.amap.api.maps.MapsInitializer
import com.bitat.MainCo
import com.bitat.config.OneClickCfg
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.CuRes
import com.bitat.repository.dto.req.OneClickLoginDto
import com.bitat.repository.dto.req.PhoneLoginDto
import com.bitat.repository.dto.resp.LoginResDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.store.BaseStore
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.state.LoginState
import com.bitat.state.LoginType
import com.bitat.state.OneClickState
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.helper.UnifyUiConfig
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

const val MAX_TIMER = 60;


class LoginViewModel() : ViewModel() {
    val loginState = MutableStateFlow(LoginState())

    val oneClickState = MutableStateFlow(OneClickState())

    private var timer: Timer? = null
    var timerDuration = MutableStateFlow(0)
    var timerContent = mutableStateOf("获取验证码")

    private val _canLogin =
        MutableStateFlow(!(loginState.value.phone == "" || loginState.value.captcha == ""))
    val canLogin: StateFlow<Boolean> get() = _canLogin.asStateFlow()

    val oneClickToken = mutableStateOf("")
    val oneClickAccessCode = mutableStateOf("")

    private val quickLogin = QuickLogin.getInstance()

    fun setTmpAccount() {
        BaseStore.setStr("phone", loginState.value.phone)
    }

    fun showDialog() {
        loginState.update {
            it.copy(isShowDialog = true)
        }
    }

    fun hideDialog() {
        loginState.update {
            it.copy(isShowDialog = false)
        }
    }


    init {
        loginState.update {
            it.copy(phone = "12222222222", captcha = "9527")
        }
    }

    fun initOneClick(context: Context) {
        try {
            quickLogin.init(context, OneClickCfg.BUSINESS_ID)

            oneClickState.update {
                it.copy(initialized = true, supported = true)
            }

            quickLogin.prefetchMobileNumber(getNumberListener)

            val builder: UnifyUiConfig.Builder = UnifyUiConfig.Builder()
            quickLogin.setUnifyUiConfig(builder.build(context))

        } catch (e: Exception) {
            CuLog.error(CuTag.Login, "there goes some exception $e", e)
        }
    }

    fun oneClick() {
        quickLogin.onePass(getTokenListener)
    }

    fun switchLoginType(type: LoginType) {
        loginState.update {
            it.copy(type = type)
        }
    }

    suspend fun oneClickSuccessLogin(): Deferred<CuRes<LoginResDto>> {
        val result = LoginReq.oneClick(dto = OneClickLoginDto().apply {
            token = oneClickState.value.token
            access = oneClickState.value.accessToken
            platform = 1
        })

        return result;
    }

    private val getNumberListener = object : QuickLoginPreMobileListener {
        override fun onGetMobileNumberSuccess(token: String?, phone: String?) { //            TODO("Not yet implemented")
            CuLog.debug(CuTag.Login,
                "we got pre mobile success the token and phone is $token, $phone")
            oneClickState.update {
                it.copy(supported = true,
                    initialized = true,
                    enabled = true,
                    phoneNumber = phone ?: "",
                    token = token ?: "")
            }
        }

        override fun onGetMobileNumberError(token: String?, msg: String?) { //            TODO("Not yet implemented")
            CuLog.debug(CuTag.Login, "we got pre mobile error the p0 and p1 is $token, $msg")
            oneClickState.update {
                it.copy(supported = false,
                    initialized = true,
                    enabled = false,
                    errorMsg = msg ?: "一键登录号码获取失败")
            }
        }
    }

    private val getTokenListener = object : QuickLoginTokenListener {
        override fun onGetTokenSuccess(token: String?, accessCode: String?) { //            TODO("Not yet implemented")
            CuLog.debug(CuTag.Login,
                "we got token success the token and accessCode is $token, $accessCode")
            oneClickState.update {
                it.copy(accessToken = token ?: "", signing = true)
            }

            MainCo.launch {
                val result = oneClickSuccessLogin().await()
                result.map {
                    TokenStore.initLogin(it)
                    CuLog.debug(CuTag.Login, "we got the result of $it")

                    oneClickState.update { that ->
                        that.copy(signed = true, signing = false)
                    }
                }

            }
        }

        override fun onGetTokenError(p0: String?, p1: Int, p2: String?) { //            TODO("Not yet implemented")
            CuLog.debug(CuTag.Login, "we got token error the p0 and p1 and p2 is $p0, $p1, $p2")
            oneClickState.update {
                it.copy(supported = true,
                    initialized = true,
                    enabled = false,
                    errorMsg = p0 ?: "一键登录凭证获取失败")
            }
        }
    }

    fun closeToast() {
        loginState.update {
            it.copy(isShowToast = false)
        }
    }

    fun showToast() {
        loginState.update {
            it.copy(isShowToast = true)
        }
    }

    fun showOneClickToast() {
        loginState.update {
            it.copy(isShowOneClickToast = true)
        }
    }

    fun closeOneClickToast() {
        loginState.update {
            it.copy(isShowOneClickToast = false)
        }
    }

    fun enableLogin(): Boolean {
        val phoneRegex = Regex(pattern = "\\d{11}")
        if (loginState.value.phone == "" || !loginState.value.phone.matches(phoneRegex)) {
            loginState.update {
                it.copy(toastContent = "手机号码格式错误")
            }
            return false;
        }

        if (loginState.value.captcha.length != 4) {
            loginState.update {
                it.copy(toastContent = "验证码格式错误")
            }
            return false;
        }

        if (!loginState.value.accepted) {
            loginState.update {
                it.copy(toastContent = "您还没有同意隐私政策及用户协议")
            }
            return false;
        }

        return true;
    }

    fun setTimer() {
        if (timer == null) {
            timerDuration.update {
                MAX_TIMER * 1
            }
            CuLog.debug(CuTag.Login, "初始化TIMER")
            MainCo.launch {
                timer = fixedRateTimer("captcha", false, 0, 1000L) {
                    if (timerDuration.value == 0) {
                        timer!!.cancel();
                        timerDuration.update {
                            0
                        }
                        timer = null
                        CuLog.debug(CuTag.Login, "清除TIMER")
                    } else {
                        timerDuration.update {
                            it.dec()
                        } //                        timerContent.value = "还剩${_timerDuration.value}秒"
                    }
                }
            }
        }
    }


    fun loginByCaptcha(successFn: () -> Unit, errorFn: (msg: String) -> Unit) {
        val dto = PhoneLoginDto().apply {
            phone = loginState.value.phone
            captcha = loginState.value.captcha
            platform = -1
            openId = ""
            atInfo = ""
        }
        MainCo.launch {
            val result = LoginReq.phone(dto).await()
            result.map {
                TokenStore.initLogin(it)
                UserStore.initUserInfo(it.user)
                successFn()
            }.errMap {
                errorFn(it.msg)
            }
        }
    }


    fun updatePhone(phone: String) {
        CuLog.debug(CuTag.Login, "the phone is ${loginState.value.phone}")
        loginState.update {
            it.copy(phone = phone)
        }
        CuLog.debug(CuTag.Login, "the phone is ${loginState.value.phone}")
    }

    fun updateCaptcha(captcha: String) {
        loginState.update {
            it.copy(captcha = captcha)
        }
    }

    fun toggleAccept(accepted: Boolean) {
        loginState.update {
            it.copy(accepted = accepted)
        }
    }

    //    fun verify(context: Context) {
    //        if (notHasBlueTooth()) {
    //            isSuccess = true;
    //        } else if (notHasLightSensorManager(context)) {
    //            isSuccess = true;
    //        } else if (ifFeatures()) {
    //            isSuccess = true;
    //        } else if (checkIsNotRealPhone()) {
    //            isSuccess = true;
    //        }
    //    }

    private fun ifFeatures(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.lowercase(Locale.getDefault())
            .contains("vbox") || Build.FINGERPRINT.lowercase(Locale.getDefault())
            .contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains(
            "Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith(
            "generic") && Build.DEVICE.startsWith("generic"))
    }
}