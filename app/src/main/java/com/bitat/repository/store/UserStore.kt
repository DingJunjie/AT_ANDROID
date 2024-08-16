package com.bitat.repository.store

import com.bitat.MainCo
import com.bitat.repository.dto.resp.UserDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object UserStore {
    lateinit var userInfo: UserDto
    private val userFlow = MutableSharedFlow<UserDto>()

    fun initUserInfo(userDto: UserDto) {
        userInfo = userDto
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun updateFans(newCount: Int) {
        userInfo.fans = newCount
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }
}
