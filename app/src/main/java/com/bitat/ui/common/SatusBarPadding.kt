@file:Suppress("UNUSED_EXPRESSION")

package com.bitat.ui.common

import android.app.Activity
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 *    author : shilu
 *    date   : 2024/7/30  16:11
 *    desc   :
 */

//fun StatusBarTopPadding(localView: ProvidableCompositionLocal<View>) {
//    val view = localView.current
//    val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets)
//
//
//    val statusBarTop = with(LocalDensity.current) {
//        insets.getInsets(WindowInsetsCompat.Type.statusBars()).top.toDp()
//    }
//    //        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
//
//    //        Log.i("StatusBarTopPadding","=====》状态栏高度$statusBarHeight")
//    if (statusBarTop > 0.dp) { // Add status bar top padding
//        //            DisposableEffect(true) {
//        //                view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
//        //                onDispose {
//        //                    view.updatePadding(top = 105)
//        //                }
//        //            }
//        statusBarHeight = statusBarTop
//    }
//}

var statusBarHeight = 0.dp


@Composable
inline fun StatusBar(  isLightStatus: Boolean,content: @Composable ColumnScope.() -> Unit) {

    val view = LocalView.current //这个就是判断 是否在编辑模式  然后设置了状态栏的颜色
    SideEffect {
        val window = (view.context as Activity).window
//        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightStatus  //这一样也是设置状态栏的颜色  大概意思就是看翻译
    }



}


