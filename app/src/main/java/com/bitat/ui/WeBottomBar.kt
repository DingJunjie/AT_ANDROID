package com.bitat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitat.ext.cdp
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.WeComposeTheme

@Composable
fun WeBottomBar(selected: Int, onSelectedChanged: (Int) -> Unit) {
    Row(Modifier.background(WeComposeTheme.colors.bottomBar)) {
        TabItem(if (selected == 0) "navigationbar/bottom_homed.svg" else "navigationbar/bottom_homepage-line.svg",
            "首页",
            if (selected == 0) WeComposeTheme.colors.iconCurrent else WeComposeTheme.colors.icon,
            Modifier.weight(1f).clickable {
                    onSelectedChanged(0)
                })
        TabItem(if (selected == 1) "navigationbar/bottom_search_fill.svg" else "navigationbar/bottom_search_line.svg",
            "搜索",
            if (selected == 1) WeComposeTheme.colors.iconCurrent else WeComposeTheme.colors.icon,
            Modifier.weight(1f).clickable {
                    onSelectedChanged(1)
                })
        TabItem(if (selected == 2) "navigationbar/bottom_add.svg" else "navigationbar/bottom_add.svg",
            "添加",
            if (selected == 2) WeComposeTheme.colors.iconCurrent else WeComposeTheme.colors.icon,
            Modifier.weight(1f).clickable {
                    onSelectedChanged(2)
                })
        TabItem(if (selected == 3) "navigationbar/bottom_msg-fill.svg" else "navigationbar/bottom_msg-line.svg",
            "信息",
            if (selected == 3) WeComposeTheme.colors.iconCurrent else WeComposeTheme.colors.icon,
            Modifier.weight(1f).clickable {
                    onSelectedChanged(3)
                })
        TabItem(if (selected == 4) "navigationbar/bottom_ait.svg" else "navigationbar/bottom_ait.svg",
            "我",
            if (selected == 4) WeComposeTheme.colors.iconCurrent else WeComposeTheme.colors.icon,
            Modifier.weight(1f).clickable {
                    onSelectedChanged(4)
                })
    }
}

@Composable
fun TabItem(iconId: String, title: String, tint: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) { //Icon(painterResource(iconId), title, Modifier.size(24.dp), tint = tint)
        SvgIcon(path = iconId, contentDescription = title)

        //Text(title, fontSize = 11.sp, color = tint)
    }
}

@Composable
fun TabItem(isSelect: Boolean, iconId: String, title: String, tint: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(vertical = 20.cdp),
        horizontalAlignment = Alignment.CenterHorizontally) { //Icon(painterResource(iconId), title, Modifier.size(24.dp), tint = tint)
        SvgIcon(path = iconId, contentDescription = title)

        //Text(title, fontSize = 11.sp, color = tint)
    }
}

//@Preview(showBackground = true)
@Composable
fun WeBottomBarPreview() {
    WeComposeTheme(WeComposeTheme.Theme.Light) {
        var selectedTab by remember { mutableStateOf(0) }
        WeBottomBar(selectedTab) { selectedTab = it }
    }
}
