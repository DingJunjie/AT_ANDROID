package com.bitat.repository.po

/**
 *    author : shilu
 *    date   : 2024/9/2  16:20
 *    desc   :
 */

data class SettingMenuPo(val itemIndex: Int = 0, val icon: String="",val showLeft:Boolean=false, val content: String, val showRight: Boolean = true, val rightText: String = "",val showRightText: Boolean = false,)