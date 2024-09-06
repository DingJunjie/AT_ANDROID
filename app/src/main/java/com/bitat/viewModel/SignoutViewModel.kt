package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.CancelDto
import com.bitat.repository.dto.req.SmsCaptchaDto
import com.bitat.repository.dto.resp.CaptchaDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.state.SettingState
import com.bitat.state.SignoutState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/6  11:15
 *    desc   : 注销登录 vm
 */
class SignoutViewModel : ViewModel() {
    private val _state = MutableStateFlow((SignoutState()))
    val state: StateFlow<SignoutState> get() = _state.asStateFlow()

    fun cancelUser(captcha: String, phone: String) {
        MainCo.launch {
            LoginReq.cancel(CancelDto(captcha, phone)).await().map { }.errMap {


            }
        }
    }

    fun sendMsg(phone: String, successFn: () -> Unit) {
        MainCo.launch {
            LoginReq.smsCaptcha(SmsCaptchaDto()).await().map {
                successFn()
            }.errMap {
                CuLog.error(CuTag.Profile, "smsCaptcha error code:${it.code},msg:${it.msg}")

            }
        }
    }

    fun checkMsg() {
        MainCo.launch {
            LoginReq.captcha().await().map { result ->

                if (state.value.captch == result.captcha && state.value.phone == result.checkKey) {
                    _state.update {
                        it.copy(isNext = true)
                    }
                } else {
                    _state.update {
                        it.copy(isNext = false)
                    }
                }
            }.errMap {
                CuLog.error(CuTag.Profile, "smsCaptcha error code:${it.code},msg:${it.msg}")

            }
        }

    }

    fun setPhone(phone: String) {
        _state.update {
            it.copy(phone = phone)
        }
    }

    fun setCaptch(captch: String) {
        _state.update {
            it.copy(captch = captch, isNext = captch.isNotEmpty())
        }
    }
}