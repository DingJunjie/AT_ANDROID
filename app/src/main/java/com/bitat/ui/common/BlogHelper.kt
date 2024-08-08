package com.bitat.ui.common

import com.bitat.repository.consts.Visibility


object BlogHelper {

    fun getVisibility(v: Visibility?): Int {
        return when (v) {
            Visibility.All -> 3
            Visibility.Friend -> 2
            Visibility.Self -> 1
            else -> 3
        }
    }
}