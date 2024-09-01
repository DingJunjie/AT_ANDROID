package com.bitat.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope


private val LightColorPalette = WeComposeColors(
    themeUi = themeColor,
    bottomBar = white1,
    background = white,
    divider = white3,
    chatPage = white2,
    textPrimary = black3,
    textPrimaryMe = black3,
    textSecondary = grey1,
    onBackground = black,
    icon = black,
    iconCurrent = green3,
    badge = red1,
    bubbleMe = green1,
    more = grey4,
    chatPageBgAlpha = 0f,
    success = green3,
    info = blue,
    error = red2,
    dialogBackground = white,
    placeholder = white3,
    surface = white,
    onSurface = black
)
private val DarkColorPalette = WeComposeColors(
    themeUi = themeColor,
    bottomBar = black1,
    background = black2,
    divider = black4,
    chatPage = black2,
    textPrimary = white4,
    textPrimaryMe = black6,
    textSecondary = grey1,
    onBackground = grey1,
    icon = white5,
    iconCurrent = green3,
    badge = red1,
    bubbleMe = green2,
    more = grey5,
    chatPageBgAlpha = 0f,
    success = green3,
    info = blue,
    error = red2,
    dialogBackground = dialogBackgroundLight,
    placeholder = white3,
    surface = white,
    onSurface = black
)

private val LocalWeComposeColors = compositionLocalOf {
    LightColorPalette
}

object WeComposeTheme {
    val colors: WeComposeColors
        @Composable get() = LocalWeComposeColors.current

    enum class Theme {
        Light, Dark
    }
}

@Stable
class WeComposeColors(
    themeUi: Color,
    bottomBar: Color,
    background: Color,
    divider: Color,
    chatPage: Color,
    textPrimary: Color,
    textPrimaryMe: Color,
    textSecondary: Color,
    onBackground: Color,
    icon: Color,
    iconCurrent: Color,
    badge: Color,
    bubbleMe: Color,
    more: Color,
    chatPageBgAlpha: Float,
    success: Color,
    info: Color,
    error: Color,
    dialogBackground: Color,
    placeholder: Color,
    surface: Color,
    onSurface: Color,

    ) {
    var themeUi: Color by mutableStateOf(themeUi)
        internal set
    var bottomBar: Color by mutableStateOf(bottomBar)
        private set
    var background: Color by mutableStateOf(background)
        private set
    var chatListDivider: Color by mutableStateOf(divider)
        private set
    var chatPage: Color by mutableStateOf(chatPage)
        private set
    var textPrimary: Color by mutableStateOf(textPrimary)
        private set
    var textPrimaryMe: Color by mutableStateOf(textPrimaryMe)
        private set
    var textSecondary: Color by mutableStateOf(textSecondary)
        private set
    var onBackground: Color by mutableStateOf(onBackground)
        private set
    var icon: Color by mutableStateOf(icon)
        private set
    var iconCurrent: Color by mutableStateOf(iconCurrent)
        private set
    var badge: Color by mutableStateOf(badge)
        private set
    var bubbleMe: Color by mutableStateOf(bubbleMe)
        private set
    var more: Color by mutableStateOf(more)
        private set
    var chatPageBgAlpha: Float by mutableStateOf(chatPageBgAlpha)
        private set
    var success: Color by mutableStateOf(success)
        private set
    var info: Color by mutableStateOf(info)
        private set
    var error: Color by mutableStateOf(error)
        private set
    var dialogBackground: Color by mutableStateOf(dialogBackground)
        private set

    var placeholder: Color by mutableStateOf(placeholder)
        private set


    var surface: Color by mutableStateOf(surface)
        private set

    var onSurface: Color by mutableStateOf(onSurface)
        private set

}

@Composable
fun BitComposeTheme(
    theme: WeComposeTheme.Theme = WeComposeTheme.Theme.Dark,
    content: @Composable() () -> Unit
) {
    val targetColors = when (theme) {
        WeComposeTheme.Theme.Light -> LightColorPalette
        WeComposeTheme.Theme.Dark -> DarkColorPalette
    }

    val bottomBar = animateColorAsState(targetColors.bottomBar, TweenSpec(600))
    val background = animateColorAsState(targetColors.background, TweenSpec(600))
    val chatListDivider = animateColorAsState(targetColors.chatListDivider, TweenSpec(600))
    val chatPage = animateColorAsState(targetColors.chatPage, TweenSpec(600))
    val textPrimary = animateColorAsState(targetColors.textPrimary, TweenSpec(600))
    val textPrimaryMe = animateColorAsState(targetColors.textPrimaryMe, TweenSpec(600))
    val textSecondary = animateColorAsState(targetColors.textSecondary, TweenSpec(600))
    val onBackground = animateColorAsState(targetColors.onBackground, TweenSpec(600))
    val dialogBackground = animateColorAsState(targetColors.dialogBackground, TweenSpec(600))
    val icon = animateColorAsState(targetColors.icon, TweenSpec(600))
    val iconCurrent = animateColorAsState(targetColors.iconCurrent, TweenSpec(600))
    val badge = animateColorAsState(targetColors.badge, TweenSpec(600))
    val bubbleMe = animateColorAsState(targetColors.bubbleMe, TweenSpec(600))
    val more = animateColorAsState(targetColors.more, TweenSpec(600))
    val chatPageBgAlpha = animateFloatAsState(targetColors.chatPageBgAlpha, TweenSpec(600))
    val success = animateColorAsState(targetColors.success, TweenSpec(600))
    val info = animateColorAsState(targetColors.info, TweenSpec(600))
    val error = animateColorAsState(targetColors.error, TweenSpec(600))
    val placeholder = animateColorAsState(targetColors.placeholder, TweenSpec(600))
    val surface = animateColorAsState(targetColors.surface, TweenSpec(600))
    val onSurface = animateColorAsState(targetColors.onSurface, TweenSpec(600))
    val colors = WeComposeColors(
        themeUi = themeColor,
        bottomBar = bottomBar.value,
        background = background.value,
        divider = chatListDivider.value,
        chatPage = chatPage.value,
        textPrimary = textPrimary.value,
        textPrimaryMe = textPrimaryMe.value,
        textSecondary = textSecondary.value,
        onBackground = onBackground.value,
        icon = icon.value,
        iconCurrent = iconCurrent.value,
        badge = badge.value,
        bubbleMe = bubbleMe.value,
        more = more.value,
        chatPageBgAlpha = chatPageBgAlpha.value,
        success = success.value,
        info = info.value,
        error = error.value,
        dialogBackground = dialogBackground.value,
        placeholder = placeholder.value,
        surface = surface.value,
        onSurface = onSurface.value
    )
    val colorsSch = lightColorScheme(
        primary = LightColorPalette.themeUi,
        onPrimary = Color.White,
        background = LightColorPalette.background,
        onBackground = LightColorPalette.onBackground,
        surface = LightColorPalette.surface,
        onSurface = LightColorPalette.onSurface
    )

    CompositionLocalProvider(LocalWeComposeColors provides colors) {
        MaterialTheme(
            colorScheme = colorsSch,
            shapes = shapes,
            content = {
                CompositionLocalProvider(LocalIndication provides NoIndication) {
                    content()
                }
            },
            typography = Typography
        )
    }
}

object NoIndication : Indication {
    private object NoIndicationInstance : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
        }

    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }

}