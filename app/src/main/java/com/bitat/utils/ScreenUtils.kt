package com.bitat.utils

import android.content.res.Configuration


object ScreenUtils {
    var screenHeight = 0
    var screenWidth = 0

    fun init(cfg: Configuration) {
        screenHeight = cfg.screenHeightDp
        screenWidth = cfg.screenWidthDp
    }
}