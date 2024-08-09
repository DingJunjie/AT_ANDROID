package  com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable
import  com.bitat.repository.dto.common.AtUserDto
import  com.bitat.repository.dto.common.ResourceDto

@Serializable
class CommentPartDto {
    var id: Long = 0 //评论id
    var userId: Long = 0 //用户id
    var blogId: Long = 0 //博文id
    var comments: UInt = 0u //子评论数
    var agrees: UInt = 0u //点赞数
    var content: String = "" //内容
    var ipTerritory: String = "" //属地
    var atUsers: Array<AtUserDto> = emptyArray() //at用户信息
    var resource: ResourceDto = ResourceDto() //资源
    var nickname: String = "" //用户名
    var profile: String = "" //头像
    var gender: Byte = 0 //性别
    var praise: Boolean = false //是否点赞
    var createTime: Long = 0 //发布时间
}

@Serializable
class SubCommentPartDto {
    var id: Long = 0 //子评id
    var userId: Long = 0 //用户id
    var pId: Long = 0 //一级评论id
    var toUserId: Long = 0  //回复谁
    var status: Byte = 0  //状态
    var agrees: UInt = 0u //点赞数
    var content: String = "" //内容
    var ipTerritory: String = "" //属地
    var resource: ResourceDto = ResourceDto() //资源
    var atUsers: Array<AtUserDto> = emptyArray() //艾特用户信息
    var nickname: String = "" //用户名
    var profile: String = "" //头像
    var toNickname: String = "" //回复谁用户名
    var toProfile: String = "" // 回复谁头像
    var gender: Byte = 0 //性别
    var toGender: Byte = 0 //回复谁性别
    var praise: Boolean = false //是否点赞
    var createTime: Long = 0 //发布时间
}

@Serializable
class CommentPart2Dto {
    var exposure: Int = 0 //博文曝光度
    var commentVec: Array<CommentPartDto> = emptyArray() //一级评论数组
}

@Serializable
class CommentPart1Dto {
    var comment: CommentPartDto = CommentPartDto() //一级评论
    var subComment: Array<SubCommentPartDto> = emptyArray() //二级评论数组
}

