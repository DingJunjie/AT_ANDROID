package com.bitat.state

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf

data class ProfileUiState(
    val userList: List<ProfileModel> = mutableStateListOf(),
    val drawerStateValue: DrawerValue = DrawerValue.Closed,
    val isTabbarTop: Boolean = false,
)

val PROFILE_TAB_OPTIONS = listOf<String>("作品", "相册", "收藏", "赞过")

data class ProfileModel(
    var name: String,
    var age: Int
)