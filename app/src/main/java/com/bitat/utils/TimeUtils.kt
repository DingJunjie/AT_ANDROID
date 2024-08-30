package com.bitat.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

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
           val date= DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(time))

            return date
        }
    }

    fun timeToYMD(time: Long):String{
        val date= DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(time))
        return date
    }

    /**
     * 时间差
     * 发表时间
     */
    fun timeToMD(time: Long): String {
        val second = (getNow() - time) / 1000
        return if (second < 60) {
            "刚刚"
        } else if (second < 60 * 60) {
            "${second / 60} 分钟前"
        } else if (second < 60 * 60 * 24) {
            "${second / 60 / 60} 小时前"
        } else {


            val date= DateTimeFormatter.ofPattern("MM-dd").withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(time))

            return date
        }
    }

    fun isThisYear(time: Long):Boolean{
        val currentYear=getYearFromMillis(time)
        val nowYear=getYearFromMillis(System.currentTimeMillis())
        return  currentYear==nowYear
    }

    fun getYearFromMillis(millis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendar.get(Calendar.YEAR)
    }

    fun getYearFromString(time: Long): String {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = time
      return  DateTimeFormatter.ofPattern("yyyy").withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(time))

    }

    fun getAgeByBirthday(birthday: Long): Int {
        val nowYear = getYearFromMillis(System.currentTimeMillis())

        val birthdayYear = getYearFromMillis(birthday)
        if (nowYear - birthdayYear > 0) return nowYear - birthdayYear else return 0
    }
}

