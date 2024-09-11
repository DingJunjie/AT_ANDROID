package com.bitat.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserHomeDto
import com.bitat.repository.dto.resp.UserPartDto

data class OthersState(
    val userId: Long = -1,
    val userInfo: MutableState<UserHomeDto>? = null,
    val isTabBarTop: Boolean = false,
    val isAtBottom: Boolean = false,
    val profileType: Int = 0,
)

val OTHER_TAB_OPTIONS = listOf<String>("作品", "相册")