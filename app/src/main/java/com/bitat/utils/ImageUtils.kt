package com.bitat.utils

import kotlin.math.abs

data class ImgSize(val width: Int, val height: Int)

object ImageUtils {

    private val regex = """x_(\d+)&y_(\d+)""".toRegex()

    fun getSizeFromUrl(url: String): ImgSize {
        val matchResult = regex.find(url)

        if (matchResult != null) {
            val (x, y) = matchResult.destructured
            return ImgSize(width = x.toInt(), height = y.toInt())
        } else {
            println("No match found")
            return ImgSize(0, 0)
        }
    }

    fun getResourceHeight(
        size: ImgSize,
        fixedWidth: Int,
    ): Int {
        val (x, y) = size;
        if (x == 0 || y == 0 || fixedWidth == 0) {
            return (ScreenUtils.screenWidth * 0.88 * 1.25).toInt()
        }

        if (x > y) {
            return if (abs(y / x - 9 / 16) < 0.1 || y / x < 9 / 16) {
                (0.75 * fixedWidth).toInt();
            } else if (abs(y / x - 0.75) < 0.1) {
                (0.75 * fixedWidth).toInt();
            } else {
                fixedWidth;
            }
        } else if (x == y) {
            // 正方形
            return fixedWidth;
        } else {
            // 竖图
            return if (abs(y / x - 16 / 9) < 0.22 || y / x > 16 / 9) {
                (16f / 9f * fixedWidth).toInt();
            } else if (abs(y / x - 4 / 3) < 0.1) {
                (4 / 3 * fixedWidth);
            } else {
                (4 / 3 * fixedWidth);
            }
        }
    }
}