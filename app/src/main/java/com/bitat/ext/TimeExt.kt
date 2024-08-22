package com.bitat.ext

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun Long.timestampFormat(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(pattern).format(Date(this))
}