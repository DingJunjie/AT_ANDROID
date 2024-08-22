package com.bitat.ext

import android.icu.text.DecimalFormat

fun Int.toAmountUnit(toFixedSize: Int = 1): String {
    var fixedSize = toFixedSize;
    if (toFixedSize < 0) fixedSize = 0;
    if (toFixedSize > 2) fixedSize = 2;

    val formatPattern = "#${if (fixedSize > 0) '.' else ""}".padEnd(fixedSize + 2, '#')
    val fmt = DecimalFormat(formatPattern)

    var formatedAmount = "";

    formatedAmount = when (this > 1_000_000) {
        true ->
            if (toFixedSize > 0) fmt.format(this.toDouble() / 1_000_000) + "m"
            else (this / 1_000_000).toString()

        false ->
            when (this > 1_000) {
                true -> if (toFixedSize > 0) fmt.format(this.toDouble() / 1_000) + "k"
                else (this / 1_000).toString()

                false -> this.toString()
            }
    }
    return formatedAmount;
}
