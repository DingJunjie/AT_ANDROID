package com.bitat.utils

object RelationUtils {
    fun toRelationContent(rel: Int, rev: Int): String {
        val fullRel = "${rel}0$rev"
        return when (fullRel) {
            "000" -> "关注"    // 陌生人
            "001" -> "关注"    // 被拉黑
            "100" -> "已拉黑"  // 拉黑
            "101" -> "已拉黑"  // 互相拉黑
            "002" -> "关注"    // 粉丝
            "200" -> "已关注"  // 关注
            "201" -> "已关注"  // 关注但是被拉黑
            "202" -> "互相关注"
            else -> "未知"
        }
    }
}