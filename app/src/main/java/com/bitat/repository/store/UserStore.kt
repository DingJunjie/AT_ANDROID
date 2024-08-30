package com.bitat.repository.store

import android.net.Uri
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.CodeErr
import com.bitat.repository.dto.req.UpdateCoverDto
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
    lateinit var userInfo: UserDto
    val userFlow = MutableSharedFlow<UserDto>()

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
        userInfo.birthday = birthday
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

    fun updateAvatar(uri: Uri) {
        QiNiuUtil.uploadSingleFile(uri, UPLOAD_OPS.Pub, FileType.Image) { key ->
            CuLog.info(CuTag.Profile, "update avatar file success")
            MainCo.launch(IO) {
                UserReq.updateProfile(UpdateProfileDto(key)).await().map {
                    CuLog.info(CuTag.Profile, "update avatar success")
                    userInfo.profile = it
                }.errMap {
                    CuLog.error(CuTag.Profile, "update avatar failed, ${it.msg}")
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
                }.errMap {
                    CuLog.error(CuTag.Profile, "update cover failed, ${it.msg}")
                }
            }
        }
    }
}
