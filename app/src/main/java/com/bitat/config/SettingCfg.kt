package com.bitat.config

import com.bitat.repository.po.SettingMenuPo
import com.bitat.repository.store.UserStore

/**
 *    author : shilu
 *    date   : 2024/9/2  16:33
 *    desc   : 菜单数据获取
 */

class SettingCfg() {

    companion object {
        fun getProfileMenu(): MutableList<SettingMenuPo> {
            val profileMenu = mutableListOf<SettingMenuPo>()
            profileMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 1,
                icon = "svg/viewing-history.svg",
                content = "观看历史",
                showRight = false,
                showRightText = false))
            profileMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 2,
                icon = "svg/use-report.svg",
                content = "反馈",
                showRight = false))
            profileMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 3,
                icon = "svg/set-up-fill.svg",
                content = "设置",
                showRight = false))
            return profileMenu
        }

        fun getSettingMenu(): MutableList<SettingMenuPo> {
            val settingMenu = mutableListOf<SettingMenuPo>()
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 1,
                icon = "svg/account-secure.svg",
                content = "账号与安全"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 2,
                icon = "svg/privacysetting.svg",
                content = "隐私设置"))

            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 3,
                icon = "svg/check-update.svg",
                content = "检查更新"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 4,
                icon = "svg/clear-cache.svg",
                content = "清除占用空间"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 5,
                icon = "svg/user-agreement.svg",
                content = "用户协议"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 6,
                icon = "svg/privacy-policy.svg",
                content = "隐私政策"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 7,
                icon = "svg/app-authority.svg",
                content = "应用权限"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 8,
                icon = "svg/app-statement.svg",
                content = "软件声明"))
            settingMenu.add(SettingMenuPo(showLeft = true,
                itemIndex = 9,
                icon = "svg/about-at.svg",
                content = "关于艾特"))
            return settingMenu
        }

        fun getAccountMenu(): MutableList<SettingMenuPo> {
            val settingMenu = mutableListOf<SettingMenuPo>()
            settingMenu.add(SettingMenuPo(showLeft = false,
                itemIndex = 1,
                content = "艾特号",
                showRightText = true,
                rightText = UserStore.userInfo.account, showRight = false))

            settingMenu.add(
                SettingMenuPo(itemIndex = 2,
                    content = "手机绑定",
                    showRight = true,
                    showRightText = true,
                    rightText = if (UserStore.userInfo.id > 0) UserStore.userInfo.phone else "未绑定"),
            )

            settingMenu.add(
                SettingMenuPo(itemIndex = 3,
                    content = "艾特号密码",
                    showRight = true,
                    showRightText = true,
                    rightText = "未设置"),
            )

            settingMenu.add(
                SettingMenuPo(itemIndex = 4,
                    content = "注销账号",
                    showRight = true, showRightText = true),
            )
            return settingMenu
        }
    }
}