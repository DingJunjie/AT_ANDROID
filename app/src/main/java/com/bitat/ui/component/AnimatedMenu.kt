package com.bitat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.ext.clickableWithoutRipple
import com.bitat.state.DiscoveryMenuOptions
import com.bitat.state.MenuOptions

@Composable
inline fun <reified T> AnimatedMenu(
    currentMenu: T,
    isOpen: Boolean = false,
    crossinline toggleMenu: (Boolean) -> Unit,
    crossinline switchMenu: (T) -> Unit
) where T : Enum<T>, T : MenuOptions {
    val rotateVal by animateFloatAsState(targetValue = if (isOpen) 90f else 270f, label = "")
    Surface(
        modifier = Modifier.height(60.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val entries = enumValues<T>()
            entries.forEach {
                AnimatedVisibility(isOpen || currentMenu == it) {
                    MenuItem(
                        it, currentMenu
                    ) {
                        switchMenu(it)
                    }
                }
            }
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "",
                modifier = Modifier
                    .rotate(rotateVal)
                    .clickableWithoutRipple {
                        toggleMenu(!isOpen)
                    })
        }
    }
}


@Composable
inline fun <reified T> MenuItem(
    menuOptions: T,
    currentMenu: T,
    crossinline choseMenu: () -> Unit
) where T : Enum<T>, T : MenuOptions {
    Box(modifier = Modifier
        .padding(3.dp)
        .clickable { choseMenu() }) {
        Text(
            menuOptions.getUiContent(),
            fontWeight = if (menuOptions == currentMenu) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}
