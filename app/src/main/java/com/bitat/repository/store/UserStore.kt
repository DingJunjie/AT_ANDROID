package com.bitat.repository.store

import android.net.Uri
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.CodeErr
import com.bitat.repository.dto.req.UpdateCoverDto
import com.bitat.repository.dto.req.UpdateIntroduceDto
import com.bitat.repository.dto.req.UpdateNicknameDto
import com.bitat.repository.dto.req.UpdateProfileDto
import com.bitat.repository.dto.req.UpdateUserInfoDto
import com.bitat.repository.dto.req.UserInfoDto
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.dto.resp.UserPartDto
import com.bitat.repository.http.service.UserReq
import com.bitat.state.GENDER
import com.bitat.utils.FileType
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.UPLOAD_OPS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object UserStore {
    var userInfo: UserDto = UserDto()
    var userFlow = MutableSharedFlow<UserDto>()

    fun fetchUserId(): Long = 0L

    fun refreshUser() {
        MainCo.launch {
            UserReq.userInfo(UserInfoDto(userId = userInfo.id)).await().map {
                updateByUserInfo(it)
            }
        }
    }

    fun initUserInfo(userDto: UserDto) {
        userInfo = userDto
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun updateByUserInfo(user: UserPartDto) {
        userInfo.fans = user.fans
        userInfo.address = user.address
        userInfo.agrees = user.agrees
        userInfo.blogs = user.blogs
        userInfo.follows = user.follows
        userInfo.profile = user.profile
        userInfo.cover = user.cover
        userInfo.birthday = user.birthday
        userInfo.gender = user.gender.toInt()
        userInfo.introduce = user.introduce
        userInfo.nickname = user.nickname
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun clearUser() {
        userInfo = UserDto()
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun updateNickname(name: String, onSuccess: () -> Unit, onError: (CodeErr) -> Unit) {
        if (name == userInfo.nickname) {
            return
        }

        userInfo.nickname = name
        MainCo.launch {
            UserReq.updateNickname(UpdateNicknameDto(nickname = name)).await().map {
                CuLog.info(CuTag.Profile, "update nickname success")
                onSuccess()
            }.errMap {
                CuLog.error(CuTag.Profile, "update nickname failed ${it.msg}")
                onError(it)
            }
            userFlow.emit(userInfo)
        }
    }

    fun updateAddress(address: String) {
        MainCo.launch {
            UserReq.updateInfo(UpdateUserInfoDto(address = address)).await().map {
                userInfo.address = address
                userFlow.emit(userInfo)
            }
        }
    }

    fun updateGender(gender: GENDER, onSuccess: () -> Unit, onError: (CodeErr) -> Unit) {
        MainCo.launch {
            UserReq.updateInfo(UpdateUserInfoDto(gender = GENDER.toCode(gender))).await().map {
                userInfo.gender = GENDER.toCode(gender).toInt()
                userFlow.emit(userInfo)
                onSuccess()
            }.errMap { onError(it) }
        }
    }

    fun updateFans(newCount: Int) {
        userInfo.fans = newCount
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun updateBirthday(birthday: Long, onSuccess: () -> Unit, onError: (CodeErr) -> Unit) {
        MainCo.launch {
            UserReq.updateInfo(UpdateUserInfoDto(birthday = birthday)).await().map {
                userInfo.birthday = birthday
                userFlow.emit(userInfo)
                onSuccess()
            }.errMap {
                onError(it)
            }
        }
    }

    fun updateAvatar(uri: Uri, onSuccess: () -> Unit, onError: (CodeErr) -> Unit) {
        QiNiuUtil.uploadSingleFile(uri, UPLOAD_OPS.Pub, FileType.Image) { key ->
            CuLog.info(CuTag.Profile, "update avatar file success")
            MainCo.launch(IO) {
                UserReq.updateProfile(UpdateProfileDto(key)).await().map {
                    userInfo.profile = it
                    userFlow.emit(userInfo)
                    onSuccess()
                }.errMap {
                    CuLog.error(CuTag.Profile, "update avatar failed, ${it.msg}")
                    onError(it)
                }
            }
        }
    }

    fun updateCover(uri: Uri) {
        QiNiuUtil.uploadSingleFile(uri, UPLOAD_OPS.Pub, FileType.Image) { key ->
            CuLog.info(CuTag.Profile, "update cover file success")
            MainCo.launch(IO) {
                UserReq.updateCover(UpdateCoverDto(key)).await().map {
                    CuLog.info(CuTag.Profile, "update cover success")
                    userInfo.cover = it
                    userFlow.emit(userInfo)
                }.errMap {
                    CuLog.error(CuTag.Profile, "update cover failed, ${it.msg}")
                }
            }
        }
    }

    fun updateIntroduce(introduce: String, onSuccess: () -> Unit, onError: (CodeErr) -> Unit) {
        MainCo.launch {
            UserReq.updateIntroduce(UpdateIntroduceDto(introduce = introduce)).await().map {
                userInfo.introduce = introduce
                userFlow.emit(userInfo)
                onSuccess()
            }.errMap {
                onError(it)
            }
        }
    }

}
