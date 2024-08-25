package com.bitat.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bitat.config.HomeTabCfg

/**
 *    author : shilu
 *    date   : 2024/8/1  15:54
 *    desc   :
 */
data class HomeState(
    val tabList: List<HomeTabCfg> = listOf(
        HomeTabCfg.Home,
        HomeTabCfg.Discovery,
        HomeTabCfg.Add,
        HomeTabCfg.Chat,
        HomeTabCfg.Mine
    ), val selectedIndex: Int = 0,val bottomHeight:Dp=0.dp
)