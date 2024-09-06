package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.dto.req.CancelDto
import com.bitat.repository.dto.req.SmsCaptchaDto
import com.bitat.repository.http.auth.LoginReq
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/5  18:14
 *    desc   :
 */
class AccountSecureViewMode : ViewModel() {

    fun cancelUser(captcha: String, phone: String) {
        MainCo.launch {
            LoginReq.cancel(CancelDto(captcha, phone)).await().map { }.errMap {


            }
        }
    }

    fun sendMsg(phone:String){

        MainCo.launch {
            LoginReq.smsCaptcha(SmsCaptchaDto()).await().map {  }.errMap {  }
        }

    }


}