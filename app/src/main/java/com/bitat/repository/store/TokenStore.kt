package com.bitat.repository.store

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.EXPIRED_CREDENTIAL
import com.bitat.repository.common.INVALID_CREDENTIAL
import com.bitat.repository.dto.common.TokenDto
import com.bitat.repository.dto.resp.AuthDto
import com.bitat.repository.dto.resp.LoginResDto
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.po.UserTokenPo
import com.bitat.repository.sqlDB.UserTokenDB
import com.bitat.utils.JsonUtils
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlinx.coroutines.coroutineScope as coScope

object TokenStore {
    private const val AUTHS_KEY = "UserAuths"
    private const val USER_KEY = "User"

    fun isExpired() = (getToken()?.expire ?: 0) < TimeUtils.getNow()

    var isInvalid = false

    // 定义私有变量以存储令牌和授权信息
    private var token: TokenDto? = null
    private var auths: Array<AuthDto>? = null

    private var deferred: Deferred<Unit>? = null

    private val locker = ReentrantLock()

    // 从 Keychain 中获取token
    private fun getToken(): TokenDto? {
        // 如果没过期，则从store中读取token
        if (token == null) locker.withLock {
            if (token == null) { //                token = BaseStore.getStr(TOKEN_KEY)?.let(JsonUtils::fromJson)
                UserTokenDB.getToken(UserStore.userInfo.id)?.let {
                    token = TokenDto().apply {
                        token = it.token
                        label = it.label
                        expire = it.expire
                    }
                }

            }
        }
        return token
    }

    // 设置 token 并存储到 Keychain 中
    private fun setToken(dto: TokenDto) {
        dto.expire += TimeUtils.getNow()
        this.token = dto //        val jsonData = JsonUtils.toJson(token)
        //        BaseStore.setStr(TOKEN_KEY, jsonData)

        val ut = UserTokenPo()
        ut.userId = UserStore.userInfo.id
        ut.token = dto.token
        ut.label = dto.label
        ut.expire = dto.expire
        ut.updateTime = TimeUtils.getNow()
        UserTokenDB.saveOne(ut)
    }

    private fun setUser(userDto: UserDto) {
        BaseStore.setStr(USER_KEY, Json.encodeToString(UserDto.serializer(), userDto))
    }

    fun getUser(): UserDto? {
        val user = BaseStore.getStr(USER_KEY)
        return if (user == null) {
            null
        } else Json.decodeFromString(user)
    }

    fun hasAuthority(authority: Int): Boolean {
        return getAuths()?.any { !it.isExpired() && it.v0 == authority } ?: false
    }

    // 获取授权信息，如果过期则从 Keychain 中读取
    private fun getAuths(): Array<AuthDto>? {
        if (auths == null) locker.withLock {
            if (auths == null) {
                auths = BaseStore.getStr(AUTHS_KEY)?.let(JsonUtils::fromJson)
            }
        }
        return auths
    }

    // 设置授权信息并存储到 Keychain 中
    private fun setAuths(auths: Array<AuthDto>) {
        this.auths = auths
        val jsonData = JsonUtils.toJson(auths)
        BaseStore.setStr(AUTHS_KEY, jsonData)
    }

    // 清除登录信息
    fun cleanLogin() { // 删除 Keychain 中的值
        //        BaseStore.remove(TOKEN_KEY)
        MainCo.launch(Dispatchers.IO) {
            UserTokenDB.updateToken(UserStore.userInfo.id, "", "", -1, TimeUtils.getNow())
        }
        BaseStore.remove(AUTHS_KEY) // 清空内存中的变量
        BaseStore.remove(USER_KEY)
        token = null
        auths = null
    }

    // 初始化登录信息
    fun initLogin(dto: LoginResDto) {
        setToken(dto.access)
        setAuths(dto.auths)
        setUser(dto.user)
    }

    // 刷新 token
    private suspend fun refresh() {
        val token = getToken()
        if (token != null) {
            var res = LoginReq.refresh(token).await()
            for (i in 0..3) {
                if (res.isErr() && res.getErr().code == EXPIRED_CREDENTIAL) {
                    delay(1000)
                    res = LoginReq.refresh(token).await()
                } else break
            }
            res.map {
                setToken(it.access)
                setAuths(it.auths)
            }.errMap {
                CuLog.error(CuTag.Login, "Refresh error code:${it.code},msg:${it.msg}")
                if (it.code == INVALID_CREDENTIAL) {
                    isInvalid = true
                    UserTokenDB.updateExpire(UserStore.userInfo.id, -1, TimeUtils.getNow())
                }
            }
        }
    }

    // 获取 token，如果过期则刷新
    suspend fun fetchToken(): String? = coScope {
        var token = getToken()
        if ((token?.expire ?: 0) < TimeUtils.getNow()) {
            var deferred: Deferred<Unit>?
            var isCreator = false
            locker.withLock {
                val now = TimeUtils.getNow()
                if (TokenStore.deferred == null) {
                    TokenStore.deferred = async { refresh() }
                    isCreator = true
                }
                deferred = TokenStore.deferred
            }
            deferred?.await()
            if (isCreator) locker.withLock {
                TokenStore.deferred = null
            }
        }
        token = TokenStore.token
        return@coScope if ((token?.expire ?: 0) > TimeUtils.getNow()) token?.token
        else null
    }
}




