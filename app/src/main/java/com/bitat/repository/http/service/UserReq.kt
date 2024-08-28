package com.bitat.repository.http.service

import com.bitat.repository.dto.resp.BlogPartDto
import com.bitat.repository.dto.req.*
import com.bitat.repository.dto.resp.*
import com.bitat.repository.http.Http

//用户相关
object UserReq {
    //修改头像
    suspend inline fun updateProfile(dto: UpdateProfileDto) =
        Http.post<_, String>("${Http.HOST}/service/user/updateProfile", dto)

    //修改封面
    suspend inline fun updateCover(dto: UpdateCoverDto) =
        Http.post<_, String>("${Http.HOST}/service/user/updateCover", dto)

    //修改昵称
    suspend inline fun updateNickname(dto: UpdateNicknameDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/updateNickname", dto)

    //修改简介
    suspend inline fun updateIntroduce(dto: UpdateIntroduceDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/updateIntroduce", dto)

    //获取用户信息
    suspend inline fun userInfo(dto: UserInfoDto) =
        Http.post<_, UserPartDto>("${Http.HOST}/service/user/userInfo", dto)

    //修改用户信息
    suspend inline fun updateInfo(dto: UpdateUserInfoDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/updateInfo", dto)

    //获取实名信息
    suspend inline fun selfReal() = Http.get<UserRealDto>("${Http.HOST}/service/user/selfReal")

    //通过用户ids查询用户信息
    suspend inline fun findBaseByIds(dto: FindBaseByIdsDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/findBaseByIds", dto)

    //通过id查询相册列表
    suspend inline fun photoBlogList(dto: PhotoBlogListDto) =
        Http.post<_, Array<BlogPartDto>>("${Http.HOST}/service/user/photoBlogList", dto)

    //修改用户公开性
    suspend inline fun updateVisible(dto: UpdateVisibleDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/updateVisible", dto)

    //获取用户公开性
    suspend inline fun getVisible() = Http.get<UserBase1Dto>("${Http.HOST}/service/user/getVisible")

    //获取用户关注列表
    suspend inline fun findFollowList(dto: FindPrivateDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/findFollowList", dto)

    //粉丝列表
    suspend inline fun findFansList(dto: FindPrivateDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/findFansList", dto)

    //获取用户好友列表
    suspend inline fun findFriendList(dto: FindFriendListDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/findFriendList", dto)

    //拉黑列表
    suspend inline fun findBlackList(dto: FindPrivateDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/findBlackList", dto)

    //设置别名
    suspend inline fun setAlias(dto: SetAliasDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/setAlias", dto)

    //修改手机号
    suspend inline fun phone(dto: UpdatePhoneDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/phone", dto)

    //用过手机号查询用户信息
    suspend inline fun phoneList(dto: FindPhoneListDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/phoneList", dto)

    //用户id查询用户信息
    suspend inline fun findUser(dto: FindUserDto) =
        Http.post<_, Array<UserBase1Dto>>("${Http.HOST}/service/user/findUser", dto)

    //查询我的私密博文
    suspend inline fun findPrivate(dto: FindPrivateDto) =
        Http.post<_, Array<BlogPartDto>>("${Http.HOST}/service/user/findPrivate", dto)

    //获取双向关系
    suspend inline fun getRelation(dto: GetRelation) = Http.post<_, FriendRelDto>(
        "${Http.HOST}/service/user/getRelation", dto
    )

    //随机推荐用户
    suspend inline fun randUser() = Http.get<UserBase1Dto>("${Http.HOST}/service/user/randUser")

    //修改密码
    suspend inline fun setPassWord(dto: SetPassWordDto) =
        Http.post<_, Unit>("${Http.HOST}/service/user/setPassWord", dto)
}