package com.bitat

import android.annotation.SuppressLint
import android.content.Context

/**
 * Created by ssk on 2022/4/17.
 */
object AppConfig {
    const val BASE_URL = ""
    const val APP_DESIGN_WIDTH = 750
}

@SuppressLint("StaticFieldLeak")
object Local {
    var ctx: Context? = null
}