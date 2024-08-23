package com.bitat.state

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserPartDto

data class ProfileUiState(
    val userList: List<ProfileModel> = mutableStateListOf(),
    val drawerStateValue: DrawerValue = DrawerValue.Closed,
    val isTabbarTop: Boolean = false,
    val myWorks: SnapshotStateList<BlogPartDto> = mutableStateListOf(),
    val fansList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    val followsList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
)

val PROFILE_TAB_OPTIONS = listOf("作品", "相册", "收藏", "赞过")

data class ProfileModel(
    var name: String,
    var age: Int
)