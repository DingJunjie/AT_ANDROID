package com.bitat.repository.http.auth

import com.bitat.repository.dto.common.TokenDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

object LoginReq {

    // 发送短信验证码
    suspend inline fun smsCaptcha(dto: SmsCaptchaDto) =
        Http.post<_, Unit>("${Http.HOST}/auth/login/smsCaptcha", dto, login = false)


    //手机号登录
    suspend inline fun phone(dto: PhoneLoginDto) =
        Http.post<_, LoginResDto>("${Http.HOST}/auth/login/phone", dto, login = false)


    //一键登录
    suspend inline fun oneClick(dto: OneClickLoginDto) =
        Http.post<_, LoginResDto>("${Http.HOST}/auth/login/oneClick", dto, login = false)


    //微信登录
    suspend inline fun thirdWeChat(dto: ThirdWeChatDto) =
        Http.post<_, LoginResDto>("${Http.HOST}/auth/login/thirdWeChat", dto, login = false)


    //qq登录
    suspend inline fun thirdQQ(dto: ThirdQQDto) =
        Http.post<_, LoginResDto>("${Http.HOST}/auth/login/thirdQQ", dto, login = false)


    //获取验证码
    suspend inline fun captcha() =
        Http.get<CaptchaDto>("${Http.HOST}/auth/login/captcha", login = false)

    //密码登录
    suspend inline fun password(dto: PasswordLoginDto) =
        Http.post<_, LoginResDto>("${Http.HOST}/auth/login/password", dto, login = false)


    //刷新token
    suspend inline fun refresh(dto: TokenDto) =
        Http.post<_, RefreshResDto>("${Http.HOST}/auth/login/refresh", dto, login = false)


    //退出登录
    suspend inline fun logout() = Http.get<Unit>("${Http.HOST}/auth/login/logout")

    //注销用户
    suspend inline fun cancel(dto: CancelDto) =
        Http.post<_, Unit>("${Http.HOST}/auth/login/cancel", dto)

    //获取下载凭证
    suspend inline fun uploadToken(dto: UploadTokenDto) =
        Http.post<_, String>("${Http.HOST}/censor/credential/upload", dto)

}