package com.bitat.config

import androidx.annotation.DrawableRes
import com.bitat.R

/**
 *    author : shilu
 *    date   : 2024/8/1  11:52
 *    desc   :
 */
sealed class HomeTabCfg(val route: String,
    @DrawableRes  val iconSelect: Int = 0,
    @DrawableRes  val iconUnselect: Int,
    var isShowText: Boolean = true
   ) {

    data object Home : HomeTabCfg(
        route = "home",
        iconSelect = R.drawable.nav_homed,
        iconUnselect = R.drawable.nav_homepage_line
    )

    data object Discovery : HomeTabCfg(
        route = "recommend",
        iconSelect = R.drawable.nav_search_fill,
        iconUnselect = R.drawable.nav_search_line
    )

    data object Add : HomeTabCfg(
        route = "add",
        iconSelect = R.drawable.nav_add,
        iconUnselect = R.drawable.nav_add
    )

    data object Chat : HomeTabCfg(
        route = "find",
        iconSelect = R.drawable.nav_msg_fill,
        iconUnselect = R.drawable.nav_msg_line
    )

    data object Mine : HomeTabCfg(
        route = "mine",
        iconSelect = R.drawable.nav_ait,
        iconUnselect = R.drawable.nav_ait
    )
}