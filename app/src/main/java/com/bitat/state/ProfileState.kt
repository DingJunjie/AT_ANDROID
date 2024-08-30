package com.bitat.state

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.consts.HttpLoadState
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.store.UserStore

data class ProfileUiState(
    val userList: List<ProfileModel> = mutableStateListOf(),
    val drawerStateValue: DrawerValue = DrawerValue.Closed,
    val isTabbarTop: Boolean = false,
    val myWorks: SnapshotStateList<BlogPartDto> = mutableStateListOf(),
    val fansList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    val followsList: SnapshotStateList<UserBase1Dto> = mutableStateListOf(),
    val myPraise: SnapshotStateList<BlogPartDto> = mutableStateListOf(),
    val isAtBottom: Boolean = false,
    val profileType: Int = 0,
    val httpState: HttpLoadState = HttpLoadState.Default,
    val isReq: Boolean = false, //是否正在请求
    val isFootShow: Boolean = false,
    val currentTabIndex: Int = 0,
    var user: UserDto = UserStore.userInfo,
    val updateFlag:Int=0,
    val showSuccess:Boolean=false,
    val showFail:Boolean=false
)

val PROFILE_TAB_OPTIONS = listOf("作品", "相册", "收藏", "赞过")

data class ProfileModel(
    var name: String,
    var age: Int
)

const val FEMALE_CODE = 1

const val MAN_CODE = 0

const val UNKNOWN_CODE = -1

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
        fun toCode(g: GENDER): Byte {
            return when (g) {
                Male -> MAN_CODE.toByte()
                Female -> FEMALE_CODE.toByte()
                Unknown -> UNKNOWN_CODE.toByte()
            }
        }

        fun toUIContent(gender:Int): String {
            return when (gender) {
                MAN_CODE  -> "男性"
                FEMALE_CODE-> "女性"
                UNKNOWN_CODE -> "未知"
                else -> ""
            }
        }
    }
}
