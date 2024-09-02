package com.bitat.config

import com.bitat.repository.po.SettingMenuPo

/**
 *    author : shilu
 *    date   : 2024/9/2  16:33
 *    desc   : 菜单数据获取
 */

class SettingCfg() {

    companion object {
        fun getProfileMenu(): MutableList<SettingMenuPo> {
            val profileMenu = mutableListOf<SettingMenuPo>()
            profileMenu.add(SettingMenuPo(itemIndex = 1,
                icon = "svg/viewing-history.svg",
                content = "观看历史",
                showRight = false))
            profileMenu.add(SettingMenuPo(itemIndex = 2,
                icon = "svg/use-report.svg",
                content = "反馈",
                showRight = false))
            profileMenu.add(SettingMenuPo(itemIndex = 3,
                icon = "svg/set-up-fill.svg",
                content = "设置",
                showRight = false))
            return profileMenu
        }

        fun getSettingMenu(): MutableList<SettingMenuPo> {
            val settingMenu = mutableListOf<SettingMenuPo>()
            settingMenu.add(SettingMenuPo(itemIndex = 1,icon = "svg/account-secure.svg",
                content = "账号与安全"))
            settingMenu.add(SettingMenuPo(itemIndex = 2,icon = "svg/clear-cache.svg",
                content = "清除占用空间"))
            settingMenu.add(SettingMenuPo(itemIndex = 3,icon = "svg/user-agreement.svg",
                content = "用户协议"))
            settingMenu.add(SettingMenuPo(itemIndex = 4,icon = "svg/app-statement.svg",
                content = "软件申明"))
            settingMenu.add(SettingMenuPo(itemIndex = 5,icon = "svg/about-at.svg",
                content = "关于艾特"))
            return settingMenu
        }
    }


}