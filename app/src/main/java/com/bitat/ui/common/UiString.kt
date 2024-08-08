package com.bitat.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiString {

    data class StringValue(val str: String) : UiString()

    class StringResource(
        @StringRes val resourceId: Int,
        vararg val args: Any
    ) : UiString()

    @Composable
    fun asString(): String {
        return when (this) {
            is StringValue -> {
                str
            }

            is StringResource -> {
                stringResource(id = resourceId, formatArgs = args)
            }

        }
    }
}