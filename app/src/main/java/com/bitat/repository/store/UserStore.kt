package com.bitat.repository.store

import com.bitat.MainCo
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object UserStore {
    lateinit var userInfo: UserDto
    val userFlow = MutableSharedFlow<UserDto>()

    fun initUserInfo(userDto: UserDto) {
        userInfo = userDto
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun updateByUserInfo(user: UserBase1Dto) {
        userInfo.fans = user.fans
        userInfo.address = user.address
        userInfo.agrees = user.agrees.toInt()
        userInfo.blogs = user.blogs.toInt()
        userInfo.follows = user.follows
        userInfo.profile = user.profile
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
