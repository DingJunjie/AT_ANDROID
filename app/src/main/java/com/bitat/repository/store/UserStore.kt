package com.bitat.repository.store

import com.bitat.MainCo
import com.bitat.repository.dto.resp.UserDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object UserStore {
    lateinit var userInfo: UserDto
    private val _userFlow = MutableSharedFlow<UserDto>()
    val userFlow: SharedFlow<UserDto> = _userFlow

    fun initUserInfo(userDto: UserDto) {
        userInfo = userDto
        MainCo.launch {
            _userFlow.emit(userInfo)
        }
    }

    fun updateFans(newCount: Int) {
        userInfo.fans = newCount
        MainCo.launch {
            _userFlow.emit(userInfo)
        }
    }
}
