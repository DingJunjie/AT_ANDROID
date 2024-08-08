package com.bitat.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {

    fun getNow() = System.currentTimeMillis()

    /**
     * 时间差
     * 发表时间
     */
    fun timeToText(time: Long): String {
        val second = (getNow() - time) / 1000
        return if (second < 60) {
            "刚刚"
        } else if (second < 60 * 60) {
            "${second / 60} 分钟前"
        } else if (second < 60 * 60 * 24) {
            "${second / 60 / 60} 小时前"
        } else {
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(time))
        }
    }

}