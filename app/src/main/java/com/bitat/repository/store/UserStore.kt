package com.bitat.repository.store

import android.net.Uri
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.GetUserInfoDto
import com.bitat.repository.dto.req.UpdateCoverDto
import com.bitat.repository.dto.req.UpdateNicknameDto
import com.bitat.repository.dto.req.UpdateProfileDto
import com.bitat.repository.dto.req.UpdateUserInfoDto
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.dto.resp.UserDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.http.service.UserReq
import com.bitat.state.GENDER
import com.bitat.utils.FileType
import com.bitat.utils.ImageParams
import com.bitat.utils.ImageUtils
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.UPLOAD_OPS
import com.bitat.utils.VideoUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object UserStore {
    lateinit var userInfo: UserDto
    val userFlow = MutableSharedFlow<UserDto>()

    fun refreshUser() {
        MainCo.launch {
            UserReq.getUserInfo(GetUserInfoDto(userId = userInfo.id)).await().map {
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

    fun updateNickname(name: String) {
        if (name == userInfo.nickname) {
            return
        }

        userInfo.nickname = name
        MainCo.launch {
            UserReq.updateNickname(UpdateNicknameDto(nickname = name)).await().map {
                CuLog.info(CuTag.Profile, "update nickname success")
            }.errMap {
                CuLog.error(CuTag.Profile, "update nickname failed ${it.msg}")
            }
            userFlow.emit(userInfo)
        }
    }

    fun updateGender(gender: GENDER) {
        userInfo.gender = GENDER.toCode(gender)
        MainCo.launch {
//            UserReq.updateInfo(UpdateUserInfoDto(gender = GENDER.toCode(gender))).await().map {
//            }
            userFlow.emit(userInfo)
        }
    }

    fun updateFans(newCount: Int) {
        userInfo.fans = newCount
        MainCo.launch {
            userFlow.emit(userInfo)
        }
    }

    fun updateBirthday(birthday: Long) {
        userInfo.birthday = birthday
        MainCo.launch {
            userFlow.emit(userInfo)
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
