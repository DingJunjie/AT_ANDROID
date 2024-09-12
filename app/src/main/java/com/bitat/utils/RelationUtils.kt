package com.bitat.utils

import com.bitat.repository.consts.BLACKLIST
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.consts.FOLLOWED

object RelationUtils {
    fun toRelationContent(rel: Int, rev: Int): String {
        val fullRel = "${rel}0$rev"
        return when (fullRel) {
            "000" -> "关注"    // 陌生人
            "00-1" -> "关注"    // 被拉黑
            "-100" -> "已拉黑"  // 拉黑
            "-10-1" -> "已拉黑"  // 互相拉黑
            "001" -> "回关"    // 粉丝
            "100" -> "已关注"  // 关注
            "10-1" -> "已关注"  // 关注但是被拉黑
            "101" -> "互相关注"
            else -> "未知"
        }
    }

    fun toFollowContent(rel: Int): String {
        return when (rel) {
            FOLLOWED -> "已关注"    // 陌生人
            DEFAULT -> "关注"    // 被拉黑
            BLACKLIST -> "已拉黑"  // 拉黑
            else -> "未知"
        }
    }

}