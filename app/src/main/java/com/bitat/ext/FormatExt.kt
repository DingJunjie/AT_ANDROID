package com.bitat.ext

import kotlin.time.Duration

private const val HOUR_IN_SECONDS = 3600
private const val MINUTE_IN_SECONDS = 60

fun Float.format(decimals: Int = 2): String {
    return if (rem(1) == 0f) {
        toInt().toString()
    } else {
        "%.${decimals}f".format(this)
        // %.2f: 格式说明符，用于格式化浮点数，f 表示浮点数，.2 表示小数点后保留两位数字
    }
}

fun Double.format(decimals: Int = 2): String {
    return if (rem(1) == 0.0) {
        toInt().toString()
    } else {
        "%.${decimals}f".format(this)
    }
}


fun Duration.format(isFull: Boolean = false): String {
    val hours = inWholeSeconds / HOUR_IN_SECONDS
    val minutes = (inWholeSeconds % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS
    val seconds = inWholeSeconds % MINUTE_IN_SECONDS

    return when {
        hours > 0 || isFull -> "%02d:%02d:%02d".format(hours, minutes, seconds)
        else -> "%02d:%02d".format(minutes, seconds)
        // %02d: 格式说明符，用于格式化整数。d 表示整数，02 表示如果数字少于两位，会在前面补零以达到两位数
    }
}