package com.bitat.state

/**
 *    author : shilu
 *    date   : 2024/9/6  11:22
 *    desc   :
 */
data class SignoutState(val isSmsSend: Boolean = false, val smsCountdown: Int = 60, val phone: String = "", val captch:String = "", val isNext: Boolean = false)
