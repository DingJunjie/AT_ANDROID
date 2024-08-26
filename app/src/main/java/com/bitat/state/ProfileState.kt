package com.bitat.state

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.consts.HttpLoadState
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
    val myPraise: SnapshotStateList<BlogPartDto> = mutableStateListOf(),
    val isAtBottom: Boolean = false,
    val profileType:Int=0,
    val httpState: HttpLoadState=HttpLoadState.Default,
    val isReq: Boolean=false, //是否正在请求
    val isFootShow: Boolean=false,
)

val PROFILE_TAB_OPTIONS = listOf("作品", "相册", "收藏", "赞过")

data class ProfileModel(
    var name: String,
    var age: Int
)

enum class GENDER {
    Male, Female, Unknown;

    fun toUIContent(): String {
        return when (this) {
            Male -> "男性"
            Female -> "女性"
            Unknown -> "未知"
        }
    }

    companion object {
        fun toCode(g: GENDER): Int {
            return when (g) {
                Male -> 1
                Female -> 0
                Unknown -> -1
            }
        }
    }
}
