package com.bitat.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserPartDto

data class OthersState(
    val userId: Int = -1,
    val userInfo: MutableState<UserPartDto>? = null,
    val isTabbarTop: Boolean = false
)

val OTHER_TAB_OPTIONS = listOf<String>("作品", "相册")