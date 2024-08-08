package com.bitat.repository.dto.resp

import kotlinx.serialization.Serializable

@Serializable
class MusicPart1 {
    var id: Long = 0 // 博文id
    var kind: Byte = 0 // 博文类型
    var name: String = "" // 名称
    var musicKey: String = "" // 音乐键
    var duration: Int = 0 // 持续时间
}

@Serializable
class MusicPart {
    var id: Long = 0 // ID
    var userId: Long = 0 // 用户ID
    var nickname: String = "" // 昵称
    var profile: String = "" // 用户头像
    var kind: Byte = 0 // 类型
    var name: String = "" // 名称
    var musicKey: String = "" // 音乐键
    var subtitleKey: String = "" // 字幕键
    var createTime: Long = 0 // 创建时间
    var duration: Int = 0 // 持续时间
    var cover: String = "" // 封面
    var useNum: Long = 0 // 使用次数
    var hasCollect: Boolean = false // 是否收藏
}
