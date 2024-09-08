package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.CancelDto
import com.bitat.repository.http.auth.LoginReq
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/6  15:47
 *    desc   :
 */
class CancelAgreementViewModel : ViewModel() {

    fun cancelAccount(captcha: String, phone: String, onCompleted: () -> Unit) {
        MainCo.launch {
            LoginReq.cancel(CancelDto(captcha, phone)).await().map {
                onCompleted()
            }.errMap {
                CuLog.error(CuTag.Profile, "LoginReq cancel error code:${it.code},msg:${it.msg}")
            }
        }
    }
}