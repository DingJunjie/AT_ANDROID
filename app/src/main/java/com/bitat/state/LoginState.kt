package com.bitat.state

enum class LoginType {
    Captcha,
    OneClick,
    Password,
    Wechat
}

data class LoginState(
    val phone: String = "",
    val captcha: String = "",
    val password: String = "",

    val type: LoginType = LoginType.OneClick,
    val accepted: Boolean = false,

    val isShowToast: Boolean = false,
    val isShowOneClickToast: Boolean = false,
    val toastContent: String = "",


    val isShowDialog: Boolean = false
)

data class OneClickState(
    val oneClickContent: String = "",
    val initialized: Boolean = false,
    val signing: Boolean = false,
    val supported: Boolean = false,
    val enabled: Boolean = false,
    val accessToken: String = "",
    val token: String = "",
    val phoneNumber: String = "",
    val errorMsg: String = "",

    val signed: Boolean = false
)
