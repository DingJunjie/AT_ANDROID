package com.bitat.helpers

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiStringHelper {

    data class StringValue(val str: String) : UiStringHelper()

    class StringResource(
        @StringRes val resourceId: Int,
        vararg val args: Any
    ) : UiStringHelper()

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